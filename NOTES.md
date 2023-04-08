# Client

A recipe is saved

1. A `slug` is calculated from the `title` (title -> slug)
2. A `key` is calculated from the `url` (url -> key)
3. An `entry` to the index is stored, it's a tuple of (key, slug)
4. An item is stored, the `slug` is used as the id ("key")

An url to a saved recipe is entered

1. A `key` is calculated from the url
2. The `key` is used to check the index for a stored recipe
3. The `slug` for the key is looked up.
4. Redirect to `/recipe/<slug>`

A saved recipe page is visited

1. The server renders the `slug` into a hidden div.
2. The UI retrieves the `slug` from the hidden div onload.
3. The `slug` is used to read the recipe from storage
4. The html is restored

The recipe collection page is visited

1. The index is loaded, all index values are read (`slugs`)
2. Each `slug` is used to read a recipe, retrieve data necessary to render list
3. The list is rendered.

A recipe is deleted from the collection

1. The delete operation is called with the recipe key
2. The slug is looked up from the index
3. The slug is used to clear the recipe
4. The key is removed from the index

# Scraper

Table used for scraper state.

```sql
CREATE TABLE IF NOT EXISTS urls(
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  url TEXT,
  scraped_at TEXT,
  html_content TEXT,
  UNIQUE(url) ON CONFLICT IGNORE
);
```

# Linking ingredients in directions

- For a labelled ingredient phrase (labels: qty, unit, parens, other)
- Consider all "other" labels
  - Ignore everything in parens
  - ignore non-words
  - ignore stuff after a comma
- Take the last of the remaining tokens, assume that's the ingredient
  - Alternatively do below steps for each of the remaining tokens. Backt to front.
- Take a direction phrase
- Tokenize the direction phrase, split at space
- For each token check whether it fits to the ingredient token
  - If multiple ingredient tokens match, include more ingredient tokens in match.
