# from fastapi import FastAPI
# from fastapi_admin.app import app as admin_app
# from fastapi_admin.providers.login import UsernamePasswordProvider
#
# from models import UserORM
# from config import SECRET_ADMIN
# from .models import UserAdmin, ThingAdmin, ParameterAdmin, ThingParameterAdmin, GroupAdmin, UserGroupAdmin
#
# login_provider = UsernamePasswordProvider(
#     admin_model=UserORM,
#     login_logo_url="https://preview.tabler.io/static/logo.svg"
# )
#
#
# async def init_admin(app: FastAPI):
#     await admin_app.configure(
#         redis=...,
#         logo_url="...",
#         default_locale="en_US",
#         language_switch=True,
#         admin_path="/admin",
#         template_folders=["..."],
#         providers=[
#             login_provider
#         ],
#         favicon_url="..."
#     )
#     admin_app.register_resources(UserAdmin, ThingAdmin, ParameterAdmin, ThingParameterAdmin, GroupAdmin, UserGroupAdmin)
