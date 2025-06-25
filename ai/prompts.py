from models import *

def get_ingredient_nutritional_values_prompt(ingredient: Ingredient):
    return f"""Nie dodawaj żadnych dodatkowych informacji, tylko odpowiedz na pytanie.
    Nie dodawaj także żadnych białych znaków ani nowych linii oraz nie używaj formatowania markdown.
    Daj mi przybliżone wartości odżywcze składnika na podstawie jego nazwy i jego ilości.
    Jeśli składnik nie istnieje, zwróć 0 dla wszystkich wartości odżywczych.
    Nazwa: {ingredient.name} 
    Ilość: {"1 " + ingredient.unit}
    """+"""
    Wiadomości odżywcze zapisane po angielsku powinny być w formacie JSON i zawierać tylko następujące klucze i wartości podane w formacie float:
    {
        "kcal": float,
        "proteins": float,
        "carbohydrates": float,
        "fats": float,
        "fiber": float,
        "sugar": float
    }
    Przykładowa odpowiedź:
    {
         "kcal": 200.0,
         "proteins": 10.0,
         "carbohydrates: 30.0,
         "fats": 5.0,
         "fiber": 8.0,
         "sugar": 2.0
    }
    """