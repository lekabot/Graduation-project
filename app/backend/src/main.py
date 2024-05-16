from fastapi import FastAPI
from auth.base_config import fastapi_users, auth_backend
from auth.routes import router_user
from auth.schemas import UserRead, UserCreate
from things.router import router_thing
from parameters.routes import router_parameter

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

app.include_router(router_user)
app.include_router(router_thing)
app.include_router(router_parameter)