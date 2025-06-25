from pydantic import BaseModel


class Macros(BaseModel):
    kcal: float
    proteins: float
    carbohydrates: float
    fats: float
    fiber: float
    sugar: float

class Ingredient(BaseModel):
    name: str
    unit: str
    categoryId: int
    macros: Macros
