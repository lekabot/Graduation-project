from fastapi import FastAPI
from auth.base_config import fastapi_users, auth_backend
from auth.routes import router_user
from auth.schemas import UserRead, UserCreate
from things.router import router_thing
from parameters.routes import router_parameter
from group.routes import router_group
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI(
    title="Tvoya mama fantastika",
    version="1.0.0"
)

origins = [
    "http://localhost",
    "http://localhost:1234",
    "http://192.168.31.186:1234/*"
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
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
app.include_router(router_group)