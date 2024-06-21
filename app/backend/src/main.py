import asyncio

from fastapi import FastAPI, Request, Depends, HTTPException
from auth.base_config import fastapi_users, auth_backend, current_user
from auth.routes import user_router
from auth.schemas import UserRead, UserCreate
from models import UserORM
from parameters.file import file_router
from parameters.qr_param import qr_router
from parameters.docs import docs_router
from things.router import thing_router
from parameters.routes import parameter_router
from group.routes import group_router
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse
from fastapi_admin.app import app as admin_app
from fastapi_admin.providers.login import UsernamePasswordProvider
import aioredis

# login_provider = UsernamePasswordProvider(
#     admin_model=UserORM,
#     # enable_captcha=True,
#     login_logo_url="https://preview.tabler.io/static/logo.svg"
# )

app = FastAPI(
    title="Backend",
    version="1.0.0"
)

# app.mount("/admin", admin_app)


# @app.on_event("startup")
# async def startup():
#     redis = await aioredis.create_redis_pool("redis://redis_app", encoding="utf8")
#     admin_app.configure(
#         logo_url="https://preview.tabler.io/static/logo-white.svg",
#         # template_folders=[os.path.join(BASE_DIR, "templates")],
#         providers=[login_provider],
#         redis=redis,
#     )


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

app.include_router(user_router)
app.include_router(thing_router)
app.include_router(parameter_router)
app.include_router(group_router)
app.include_router(qr_router)
app.include_router(docs_router)
app.include_router(file_router)


@app.get("/auth/jwt/token")
async def get_jwt_token(request: Request):
    token = request.cookies.get("bonds")

    if token is None:
        raise HTTPException(status_code=403, detail="Token not found in cookies")

    return JSONResponse(content={"token": token})


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
