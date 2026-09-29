const { test, expect } = require('@playwright/test');

const LOGIN_URL = 'login.html';
const USERNAME = 'admin';
const PASSWORD = 'triztech123';

// Uses the PrimeFaces selectOneMenu like a real user:
// opens the panel then clicks the wanted item.
async function choisirDansMenu(page, id, texte) {
  const selecteur = `#${id}`.replace(/:/g, '\\:');
  await page.locator(selecteur).locator('.ui-selectonemenu-label').click();
  await page.locator(`${selecteur}_panel li`).filter({ hasText: texte }).click();
}

async function seConnecter(page) {
  await page.goto(LOGIN_URL);
  await expect(page).toHaveTitle(/Connexion/);
  await page.fill('#username', USERNAME);
  await page.fill('#password', PASSWORD);
  await Promise.all([
    page.waitForURL(/\/location-voitures\/$/),
    page.click('button[type="submit"]'),
  ]);
}

test('login page is visible', async ({ page }) => {
  await page.goto('login.html');
  await expect(page).toHaveTitle(/Connexion/);
  await expect(page.locator('h2')).toHaveText('Connexion');
});

test('login with valid credentials goes to dashboard', async ({ page }) => {
  await page.goto('login.html');
  await page.fill('#username', 'admin');
  await page.fill('#password', 'triztech123');
  await page.click('button[type="submit"]');

  await expect(page).toHaveURL(/\/location-voitures\/$/);
  await expect(page.locator('h1')).toHaveText('🚗 Location de voitures');
  await expect(page.locator('.nom-utilisateur')).toHaveText('admin');
});

test('login with wrong password shows error', async ({ page }) => {
  await page.goto('login.html');
  await page.fill('#username', 'admin');
  await page.fill('#password', 'mauvais');
  await page.click('button[type="submit"]');

  await expect(page).toHaveURL(/error/);
  await expect(page.locator('#alerteErreur')).toBeVisible();
  await expect(page.locator('#alerteErreur')).toContainText('Identifiants incorrects');
});

test('logout returns to login page', async ({ page }) => {
  await page.goto('login.html');
  await page.fill('#username', 'admin');
  await page.fill('#password', 'triztech123');
  await page.click('button[type="submit"]');

  await page.click('.bouton-logout');
  await expect(page).toHaveURL(/login\.html/);
  await expect(page.locator('#alerteInfo')).toContainText('Vous avez été déconnecté');
});

test('nouvelle location passe la voiture de disponible a occupe', async ({ page }) => {
  await seConnecter(page);

  // ---- Onglet "Nouvelle location" ----
  await page.evaluate(() => PF('onglets').select(1));
  await page.waitForTimeout(800);

  // Choix d'une voiture disponible (la première réelle) depuis le <select> caché
  const nomsVoitures = await page.evaluate(() => {
    const s = document.getElementById('formulaire:j_idt7:j_idt34_input');
    return Array.from(s.options).map((o) => o.textContent.trim());
  });
  const voiture = nomsVoitures.find((t) => t && !t.startsWith('Choisir'));
  const nomVoiture = voiture.split(' - ')[0];
  expect(voiture).toBeTruthy();

  await choisirDansMenu(page, 'formulaire:j_idt7:j_idt34', nomVoiture);
  await page.waitForTimeout(600);

  // Le résumé doit recalculer un prix total non nul
  const resume = await page.textContent('#formulaire\\:j_idt7\\:resume');
  expect(resume).toContain('Prix total');
  expect(resume).not.toContain('Prix total : 0 DZD');

  // Choix d'un client (premier de la liste) depuis le <select> caché
  const nomsClients = await page.evaluate(() => {
    const s = document.getElementById('formulaire:j_idt7:j_idt38_input');
    return Array.from(s.options).map((o) => o.textContent.trim());
  });
  const client = nomsClients.find((t) => t && !t.startsWith('Choisir'));
  await choisirDansMenu(page, 'formulaire:j_idt7:j_idt38', client);
  await page.waitForTimeout(400);

  // Remplissage du reste du formulaire
  await page.fill('#formulaire\\:j_idt7\\:j_idt46', 'Aeroport Houari Boumediene');
  await page.fill('#formulaire\\:j_idt7\\:j_idt48', 'Agence Bab Ezzouar');
  await page.fill('#formulaire\\:j_idt7\\:j_idt50_input', '100');

  // Dates (début = aujourd'hui +1, fin = aujourd'hui +4)
  const loc = await page.evaluate(() => {
    const ajd = new Date();
    const fmt = (d) => {
      const j = String(d.getDate()).padStart(2, '0');
      const m = String(d.getMonth() + 1).padStart(2, '0');
      return `${j}/${m}/${d.getFullYear()}`;
    };
    const debut = new Date(ajd);
    debut.setDate(ajd.getDate() + 1);
    const fin = new Date(ajd);
    fin.setDate(ajd.getDate() + 4);
    return { debut: fmt(debut), fin: fmt(fin) };
  });
  await page.fill('#formulaire\\:j_idt7\\:j_idt42_input', loc.debut);
  await page.locator('#formulaire\\:j_idt7\\:j_idt42_input').press('Enter');
  await page.fill('#formulaire\\:j_idt7\\:j_idt44_input', loc.fin);
  await page.locator('#formulaire\\:j_idt7\\:j_idt44_input').press('Enter');
  await page.waitForTimeout(600);

  // ---- Enregistrement ----
  await page.locator('#formulaire\\:j_idt7\\:j_idt52').click();
  await page.waitForTimeout(1500);

  // Message de confirmation visible
  await expect(page.locator('.ui-growl')).toContainText('Location confirmée');

  // ---- La voiture n'apparaît plus parmi les disponibles ----
  const nomsApres = await page.evaluate(() => {
    const s = document.getElementById('formulaire:j_idt7:j_idt34_input');
    return Array.from(s.options).map((o) => o.textContent.trim());
  });
  expect(nomsApres).not.toContain(voiture);

  // ---- Onglet "Voitures" : la voiture doit être dans la liste Occupées ----
  await page.evaluate(() => PF('onglets').select(2));
  await page.waitForTimeout(800);

  // Filtre sur le statut OCCUPE
  await choisirDansMenu(page, 'formulaire:j_idt7:j_idt60', 'OCCUPE');
  await page.waitForTimeout(800);

  // Recherche de la voiture : le filtre écoute l'événement keyup (LIKE sur la
  // marque OU le modèle), on tape donc réellement les caractères puis un mot
  // unique de la marque pour un match fiable.
  const marqueVoiture = nomVoiture.split(/[\s]+/)[0];
  await page.locator('#formulaire\\:j_idt7\\:j_idt56').pressSequentially(marqueVoiture);
  await page.waitForTimeout(800);

  // Au moins une ligne portant cette marque doit être présente et en statut OCCUPE
  const lignesVoiture = page
    .locator('#formulaire\\:j_idt7\\:tableVoitures tbody tr')
    .filter({ hasText: marqueVoiture });
  await expect(lignesVoiture, 'la voiture doit apparaître dans la liste').not.toHaveCount(0);
  await expect(
    lignesVoiture.locator('td').nth(6),
    'le statut doit être OCCUPE'
  ).toContainText(['OCCUPE']);
});