from fastapi import FastAPI
from auth.base_config import fastapi_users, auth_backend
from auth.schemas import UserRead, UserCreate

app = FastAPI(
    title="Tvoya mama fantastika",
    version="1.0.0"
)

app.include_router(
    fastapi_users.get_auth_router(auth_backend),
    prefix="/auth",
    tags=["Auth"],
)

app.include_router(
    fastapi_users.get_register_router(UserRead, UserCreate),
    prefix="/auth",
    tags=["Auth"],
)