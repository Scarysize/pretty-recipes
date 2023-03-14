import re
import html
from fractions import Fraction

STRING_NUMBERS = {
    "half": "0.5",
    "one": "1",
    "two": "2",
    "three": "3",
    "four": "4",
    "five": "5",
    "six": "6",
    "seven": "7",
    "eight": "8",
    "nine": "9",
}


def replace_string_numbers(sentence: str) -> str:
    for s, n in STRING_NUMBERS.items():
        sentence = re.sub(rf"\b{s}\b", rf"{n}", sentence, flags=re.IGNORECASE)

    return sentence


def replace_html_fractions(sentence: str) -> str:
    return html.unescape(sentence)


def replace_unicode_fractions(sentence: str) -> str:
    fractions = {
        "\u215b": "1/8",
        "\u215c": "3/8",
        "\u215d": "5/8",
        "\u215e": "7/8",
        "\u2159": "1/6",
        "\u215a": "5/6",
        "\u2155": "1/5",
        "\u2156": "2/5",
        "\u2157": "3/5",
        "\u2158": "4/5",
        "\xbc": "1/4",
        "\xbe": "3/4",
        "\u2153": "1/3",
        "\u2154": "2/3",
        "\xbd": "1/2",
    }

    for f_unicode, f_ascii in fractions.items():
        sentence = sentence.replace(f_unicode, f" {f_ascii}")

    return sentence


FRACTION_PARTS_PATTERN = re.compile(r"(\d*\s*\d/\d+)")


def replace_fake_fractions(sentence: str) -> str:
    matches = FRACTION_PARTS_PATTERN.findall(sentence)
    # This is a bit of a hack.
    # If a fraction appears multiple times but in different forms e.g. 1/2 and
    # 1 1/2, then
    # we need to replace the longest one first, otherwise both instance of 1/2
    # would be replaced at the same time which would mean that the instance of
    # 1 1/2 would end up as 1 0.5 instead of 1.5
    matches.sort(key=len, reverse=True)

    if not matches:
        return sentence

    for match in matches:
        # The regex pattern will capture the space before a fraction if the fraction
        # doesn't have a whole number in front of it.
        # Therefore, if the match starts with a space, remove it.
        if match.startswith(" "):
            match = match[1:]

        split = match.split()
        summed = float(sum(Fraction(s) for s in split))
        rounded = round(summed, 3)
        sentence = sentence.replace(match, f"{rounded:g}")

    return sentence


QUANTITY_UNITS_PATTERN = re.compile(r"(\d)([a-zA-Z])")


def split_quantity_and_units(sentence: str) -> str:
    return QUANTITY_UNITS_PATTERN.sub(r"\1 \2", sentence)


STRING_RANGE_PATTERN = re.compile(r"([\d\.]+)(-)?\s+(to|or)\s+([\d\.]+(-)?)")


def replace_string_range(sentence: str) -> str:
    return STRING_RANGE_PATTERN.sub(r"\1-\4", sentence)


UNITS = {
    "bags": "bag",
    "bars": "bar",
    "bottles": "bottle",
    "boxes": "box",
    "branches": "branch",
    "bulbs": "bulb",
    "bunches": "bunch",
    "cans": "can",
    "chops": "chop",
    "chunks": "chunk",
    "cloves": "clove",
    "clusters": "cluster",
    "cubes": "cube",
    "cups": "cup",
    "dashes": "dash",
    "dollops": "dollop",
    "drops": "drop",
    "ears": "ear",
    "envelopes": "envelope",
    "feet": "foot",
    "fillets": "fillet",
    "gallons": "gallon",
    "glasses": "glass",
    "grams": "gram",
    "grinds": "grind",
    "handfuls": "handful",
    "heads": "head",
    "inches": "inch",
    "jars": "jar",
    "kilograms": "kilogram",
    "knobs": "knob",
    "lbs": "lb",
    "leaves": "leaf",
    "lengths": "length",
    "links": "link",
    "liters": "liter",
    "litres": "litre",
    "loaves": "loaf",
    "milliliters": "milliliter",
    "ounces": "ounce",
    "packs": "pack",
    "packages": "package",
    "packets": "packet",
    "pairs": "pair",
    "pieces": "piece",
    "pinches": "pinch",
    "pints": "pint",
    "pounds": "pound",
    "racks": "rack",
    "rectangles": "rectangle",
    "quarts": "quart",
    "scoops": "scoop",
    "segments": "segment",
    "shakes": "shake",
    "sheets": "sheet",
    "shoots": "shoot",
    "slabs": "slab",
    "slices": "slice",
    "sprigs": "sprig",
    "squares": "square",
    "stalks": "stalk",
    "steaks": "steak",
    "stems": "stem",
    "sticks": "stick",
    "strips": "strip",
    "tablespoons": "tablespoon",
    "tbsps": "tbsp",
    "teaspoons": "teaspoon",
    "tsps": "tsp",
    "twists": "twist",
    "wedges": "wedge",
}


def singularize_unit(sentence: str) -> str:
    for plural, singular in UNITS.items():
        sentence = sentence.replace(plural, singular)
        sentence = sentence.replace(plural.capitalize(), singular.capitalize())

    return sentence


def normalize_input(sentence: str) -> str:
    s = replace_string_numbers(sentence)
    s = replace_html_fractions(s)
    s = replace_unicode_fractions(s)
    s = replace_fake_fractions(s)
    s = split_quantity_and_units(s)
    s = replace_string_range(s)
    s = singularize_unit(s)
    return s
