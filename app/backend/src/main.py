from fastapi import FastAPI, Request, Depends
from auth.base_config import fastapi_users, auth_backend, current_user
from auth.routes import router_user
from auth.schemas import UserRead, UserCreate
from models import UserORM
from things.router import router_thing
from parameters.routes import router_parameter
from group.routes import router_group
from fastapi.middleware.cors import CORSMiddleware

app = FastAPI(
    title="Tvoya mama fantastika",
    version="1.0.0"
)

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


@app.middleware("http")
async def log_requests(request: Request, call_next):
    print(f"Headers: {request.headers}")
    print(f"Cookies: {request.cookies}")
    response = await call_next(request)
    return response


@app.get("/protected-route")
async def protected_route(user: UserORM = Depends(current_user)):
    print(f"Authenticated user: {user}")
    return {"message": "You are authenticated!"}