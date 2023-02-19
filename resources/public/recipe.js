const INDEX_KEY = 'pretty-recipes.index';

function urlToKey(url) {
  return btoa(url);
}

function titleToSlug(title) {
  return title
    .toString()
    .normalize('NFD')
    .replace(/[\u0300-\u036f]/g, '')
    .toLowerCase()
    .trim()
    .replace(/\s+/g, '-')
    .replace(/[^\w-]+/g, '')
    .replace(/--+/g, '-');
}

function readIndex() {
  const content = localStorage.getItem(INDEX_KEY) || '[]';
  const index = JSON.parse(content);

  return new Map(index);
}

function writeIndex(indexMap) {
  const index = Array.from(indexMap.entries());
  const content = JSON.stringify(index);

  localStorage.setItem(INDEX_KEY, content);
}

function addToIndex(key, slug) {
  const index = readIndex();

  if (index.has(key)) return;

  index.set(key, slug);
  writeIndex(index);
}

function removeFromIndex(key) {
  const index = readIndex();
  index.delete(key);
  writeIndex(index);
}

function writeRecipe(slug, recipe) {
  const fullKey = `pretty-recipes.recipe.${slug}`;
  const content = JSON.stringify(recipe);

  localStorage.setItem(fullKey, content);
}

function deleteRecipe(slug) {
  const fullKey = `pretty-recipes.recipe.${slug}`;
  localStorage.removeItem(fullKey);
}

function readRecipe(slug) {
  const fullKey = `pretty-recipes.recipe.${slug}`;
  const content = localStorage.getItem(fullKey);

  if (content) {
    return JSON.parse(content);
  } else {
    return null;
  }
}

function readAllRecipes() {
  const index = readIndex();

  const recipes = [];
  for (const slug of index.values()) {
    const recipe = readRecipe(slug);
    recipes.push([slug, recipe]);
  }

  return recipes;
}

function saveRecipe() {
  const index = readIndex();
  const saveButton = document.querySelector('.save-btn');

  const url = document.getElementById('recipe-source').href;
  const title = document.getElementById('recipe-title').textContent;

  const key = urlToKey(url);
  if (index.has(key)) {
    return;
  }

  const randomPart = (Date.now() % 100000).toString().padStart(5, '0');
  const slug = titleToSlug(title) + '-' + randomPart;

  const ingredients = document.getElementById('recipe-ingredients');
  const directions = document.getElementById('recipe-directions');

  const recipe = {
    title,
    source: url,
    ingredients: ingredients.outerHTML,
    directions: directions.outerHTML,
    timestamp: Date.now(),
  };

  addToIndex(key, slug);
  writeRecipe(slug, recipe);

  toggleSaveButton(true);
}

function unsaveRecipe() {
  const index = readIndex();
  const url = document.getElementById('recipe-source').href;
  const key = urlToKey(url);
  const slug = index.get(key);

  deleteRecipe(slug);
  removeFromIndex(key);

  toggleSaveButton(false);
}

function toggleSaveButton(recipeWasSaved) {
  const saveButton = document.querySelector('.save-btn');

  saveButton.onclick = null;
  saveButton.removeEventListener('click', unsaveRecipe);
  saveButton.removeEventListener('click', saveRecipe);

  if (recipeWasSaved) {
    saveButton.classList.add('save-btn--saved');
    saveButton.textContent = 'Saved';
    saveButton.addEventListener('click', unsaveRecipe);
  } else {
    saveButton.classList.remove('save-btn--saved');
    saveButton.textContent = 'Save';
    saveButton.addEventListener('click', saveRecipe);
  }
}

function restoreRecipe() {
  const saveButton = document.querySelector('.save-btn');
  const slugElement = document.querySelector('div[data-slug]');

  if (!slugElement) {
    return;
  }

  const slug = slugElement.dataset.slug;
  const recipe = readRecipe(slug);

  if (!recipe) {
    location.href = '/';
    return;
  }

  toggleSaveButton(true);

  document.getElementById('recipe-title').textContent = recipe.title;

  const source = document.getElementById('recipe-source');
  source.textContent = recipe.source;
  source.href = recipe.source;

  document.getElementById('recipe-ingredients').outerHTML = recipe.ingredients;
  document.getElementById('recipe-directions').outerHTML = recipe.directions;
}

function submitRecipe(event) {
  const form = document.querySelector('form');
  form.reportValidity();

  const urlInput = document.querySelector('input[type=url]');
  const url = urlInput.value;
  const key = urlToKey(url);
  const index = readIndex();
  const slug = index.get(key);

  if (slug) {
    event.preventDefault();
    window.location.href = `/recipe/${slug}`;
  }
}

function renderCollection() {
  const container = document.getElementById('recipe-collection');
  const recipes = readAllRecipes();

  if (!recipes.length) {
    const message = document.createElement('p');
    message.textContent = "You haven't saved any recipes yet.";
    container.appendChild(message);
    return;
  }

  const list = document.createElement('ul');
  list.classList.add('recipe-list');

  recipes.forEach(([slug, recipe]) => {
    const item = document.createElement('li');
    const anchor = document.createElement('a');
    const host = document.createElement('span');

    host.textContent = `(${new URL(recipe.source).hostname.replace(
      /^www\./,
      ''
    )})`;
    host.classList.add('recipe-host');

    anchor.href = `/recipe/${slug}`;
    anchor.textContent = recipe.title;

    item.appendChild(anchor);
    item.appendChild(host);

    list.appendChild(item);
  });

  container.appendChild(list);
}

const main = document.querySelector('main');
if (main.classList.contains('recipe')) {
  restoreRecipe();
} else if (main.classList.contains('collection')) {
  renderCollection();
}
