from fastapi import FastAPI, Request, Depends, HTTPException
from auth.base_config import fastapi_users, auth_backend, current_user
from auth.routes import user_router
from auth.schemas import UserRead, UserCreate
from models import UserORM
from parameters.qr_param import qr_router
from parameters.docs import docs_router
from things.router import thing_router
from parameters.routes import parameter_router
from group.routes import group_router
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse

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

app.include_router(user_router)
app.include_router(thing_router)
app.include_router(parameter_router)
app.include_router(group_router)
app.include_router(qr_router)
app.include_router(docs_router)


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
