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


class ParameterDelete(BaseModel):
    thing_title: str
    key: str
