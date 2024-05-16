from pydantic import BaseModel


class UserEmptyGroupCreate(BaseModel):
    user_id: int
    username: str


class UserGroupRead(BaseModel):
    id: int
    title: str
    url: str
    owner_id: int

    class Config:
        orm_mode = True
        from_attributes = True