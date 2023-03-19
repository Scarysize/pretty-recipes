function fetchNext() {
  return fetch('/ingredients.getNextEmpty').then(response => response.json());
}

const $ingredientInput = document.getElementById('ingredient-input');

const LABEL_NAME = 'name';
const LABEL_QUANTITY = 'qty';
const LABEL_RANGE_END = 'range_end';
const LABEL_UNIT = 'unit';
const LABEL_COMMENT = 'comment';

const state = {
  ingredient: null,
  tokens: [],
  labels: [],
  selected: 0,
};

function render(s) {
  const $tokenSpans = [...document.querySelectorAll('#ingredient-input span')];
  $tokenSpans.forEach(($t, i) => {
    if (i === s.selected) $t.classList.add('selected');
    else $t.classList.remove('selected');

    const label = s.labels[i];
    const labelClass = 'labelled';
    if (label) {
      $t.classList.add(labelClass);
      $t.dataset.label = label;
    } else {
      $t.classList.remove(labelClass);
      $t.dataset.label = '';
    }
  });
}

async function next() {
  state.ingredient = await fetchNext();
  state.tokens = state.ingredient.input.split(' ');
  state.labels = new Array(state.tokens.length).fill(null);

  $ingredientInput.childNodes.forEach(child =>
    child.parentNode.removeChild(child)
  );
  state.tokens.forEach(token => {
    const $tokenSpan = document.createElement('span');
    $tokenSpan.textContent = token;
    $ingredientInput.appendChild($tokenSpan);
  });

  render(state);
}

next();

const labelKeyMapping = {
  n: LABEL_NAME,
  q: LABEL_QUANTITY,
  r: LABEL_RANGE_END,
  u: LABEL_UNIT,
  c: LABEL_COMMENT,
};

window.addEventListener('keydown', e => {
  const key = e.key.toLowerCase();

  if (key === 'j') {
    state.selected = (state.selected + 1) % state.tokens.length;
  } else if (labelKeyMapping[key]) {
    const label = labelKeyMapping[key];
    state.labels[state.selected] = label;
    state.selected = (state.selected + 1) % state.tokens.length;
  }

  render(state);
});
