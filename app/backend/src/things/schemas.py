from pydantic import BaseModel


class ThingRead(BaseModel):
    id: int
    title: str

    class Config:
        orm_mode = True
        from_attributes = True


class ThingCreate(BaseModel):
    title: str
