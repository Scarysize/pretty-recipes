import nltk
from nltk import pos_tag
from nltk.corpus import stopwords

from prep import normalize_input, UNITS

nltk.download('stopwords')
STOP_WORDS = set(stopwords.words('english'))

TOKENIZER = nltk.tokenize.RegexpTokenizer(r"[\w\.\-\']+|\(|\)|,|\"", gaps=False)

def extract_ingredients(model, phrase: str):
  normalized_phrase = normalize_input(phrase)
  tokens = TOKENIZER.tokenize(normalized_phrase)
  features = [extract_feature(token, index, tokens) for (index, token) in enumerate(tokens)]
  prediction = model.predict([features])

  return list(zip(prediction[0], tokens))

def is_inside_parens(index: int, all_tokens):
    if all_tokens[index] in ["(", ")"]:
        return True

    return "(" in all_tokens[:index] and ")" in all_tokens[index + 1 :]

def extract_feature(token: str, i: int, all_tokens):
    prev1_features = {}
    prev2_features = {}
    next1_features = {}
    next2_features = {}
    bio_tag = "B"

    if i > 0:
        prev1_features["-1:pos_tag"] = pos_tag(all_tokens)[i - 1][1]
        prev1_features["-1:word"] = all_tokens[i - 1].lower()
        bio_tag = "I"
    if i > 1:
        prev2_features["-2:pos_tag"] = pos_tag(all_tokens)[i - 2][1]
        prev2_features["-2:word"] = all_tokens[i - 2].lower()
    if i < len(all_tokens) - 1:
        next1_features["+1:pos_tag"] = pos_tag(all_tokens)[i + 1][1]
        next1_features["+1:word"] = all_tokens[i + 1].lower()
    if i < len(all_tokens) - 2:
        next2_features["+2:pos_tag"] = pos_tag(all_tokens)[i + 2][1]
        next2_features["+2:word"] = all_tokens[i + 2].lower()
    if i == len(all_tokens) - 1:
        bio_tag = "O"

    features = {
        "word": token.lower(),
        "is_digit": token.isdigit(),
        "is_upper": token.isupper(),
        "is_lower": token.islower(),
        "is_unit": token.lower() in UNITS,
        "is_in_parens": is_inside_parens(i, all_tokens),
        "is_stop_word": token.lower() in STOP_WORDS,
        "length": len(token),
        "pos_tag": pos_tag(all_tokens)[i][1],
        "bio_tag": bio_tag,
        "index": i,
    }

    return features | prev1_features | prev2_features | next1_features | next2_features
