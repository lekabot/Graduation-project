from pydantic import BaseModel


class ParameterRead(BaseModel):
    id: int
    key: str
    value: str


class ParameterUpdate(BaseModel):
    key: str
    value: str


class ParameterCreate(BaseModel):
    key: str
    value: str
