from fastapi_users import FastAPIUsers
from fastapi_users.authentication import (AuthenticationBackend,
                                          CookieTransport, JWTStrategy)

from auth.manager import get_user_manager
from models import UserORM
from config import SECRET_AUTH

cookie_transport = CookieTransport(
    cookie_name="bonds",
    cookie_max_age=3600,
    cookie_secure=False,
    cookie_samesite="lax"
)


def get_jwt_strategy() -> JWTStrategy:
    return JWTStrategy(secret=SECRET_AUTH, lifetime_seconds=3600)


auth_backend = AuthenticationBackend(
    name="jwt",
    transport=cookie_transport,
    get_strategy=get_jwt_strategy,
)

fastapi_users = FastAPIUsers[UserORM, int](
    get_user_manager,
    [auth_backend],
)

current_user = fastapi_users.current_user()
