from pydantic import BaseModel
from datetime import datetime


class ParameterRead(BaseModel):
    id: int
    key: str
    value: str

    class Config:
        orm_mode = True
        from_attributes = True


class ParameterAuthoriz(BaseModel):
    thing_title: str
    key: str
    value: str


class ParameterUpdate(BaseModel):
    key: str
    value: str


class ParameterCreate(BaseModel):
    key: str
    value: str

    class Config:
        orm_mode = True
        from_attributes = True


class ParameterDelete(BaseModel):
    thing_title: str
    key: str
    value: str


class ParameterThingRead(BaseModel):
    thing_id: int
    parameter_id: int

    class Config:
        orm_mode = True
        from_attributes = True

