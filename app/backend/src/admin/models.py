# from fastapi_admin.resources import Model, Field
# from fastapi_admin.widgets import displays, inputs, filters
#
# from models import UserORM, ThingORM, ParameterORM, ThingParameterORM, GroupORM, UserGroupORM
# from fastapi_admin.app import app
#
#
# # Admin model for UserORM
# @app.register
# class UserAdmin(Model):
#     label = "Users"
#     model = UserORM
#     icon = "fas fa-user"
#     page_pre_title = "user list"
#     page_title = "user model"
#     filters = [
#         filters.Search(name="username", label="Username", search_mode="contains", placeholder="Search for username"),
#         filters.Search(name="email", label="Email", search_mode="contains", placeholder="Search for email"),
#     ]
#     fields = [
#         "id",
#         Field(name="username", label="Username", display=displays.Display(), input_=inputs.Text()),
#         Field(name="email", label="Email", display=displays.Display(), input_=inputs.Email()),
#         Field(name="hashed_password", label="Password", display=displays.InputOnly(), input_=inputs.Password()),
#     ]
#
#
# # Admin model for ThingORM
# @app.register
# class ThingAdmin(Model):
#     label = "Things"
#     model = ThingORM
#     icon = "fas fa-box"
#     page_pre_title = "thing list"
#     page_title = "thing model"
#     filters = [
#         filters.Search(name="title", label="Title", search_mode="contains", placeholder="Search for title"),
#     ]
#     fields = [
#         "id",
#         Field(name="title", label="Title", display=displays.Display(), input_=inputs.Text()),
#     ]
#
#
# # Admin model for ParameterORM
# @app.register
# class ParameterAdmin(Model):
#     label = "Parameters"
#     model = ParameterORM
#     icon = "fas fa-cogs"
#     page_pre_title = "parameter list"
#     page_title = "parameter model"
#     filters = [
#         filters.Search(name="key", label="Key", search_mode="contains", placeholder="Search for key"),
#         filters.Search(name="value", label="Value", search_mode="contains", placeholder="Search for value"),
#     ]
#     fields = [
#         "id",
#         Field(name="key", label="Key", display=displays.Display(), input_=inputs.Text()),
#         Field(name="value", label="Value", display=displays.Display(), input_=inputs.Text()),
#     ]
#
#
# # Admin model for ThingParameterORM
# @app.register
# class ThingParameterAdmin(Model):
#     label = "Thing Parameters"
#     model = ThingParameterORM
#     icon = "fas fa-link"
#     page_pre_title = "thing parameter list"
#     page_title = "thing parameter model"
#     filters = [
#         filters.Search(name="thing_id", label="Thing ID", search_mode="contains", placeholder="Search for thing ID"),
#         filters.Search(name="parameter_id", label="Parameter ID", search_mode="contains",
#                        placeholder="Search for parameter ID"),
#     ]
#     fields = [
#         "id",
#         Field(name="thing_id", label="Thing ID", display=displays.Display(), input_=inputs.Text()),
#         Field(name="parameter_id", label="Parameter ID", display=displays.Display(), input_=inputs.Text()),
#     ]
#
#
# # Admin model for GroupORM
# @app.register
# class GroupAdmin(Model):
#     label = "Groups"
#     model = GroupORM
#     icon = "fas fa-users"
#     page_pre_title = "group list"
#     page_title = "group model"
#     filters = [
#         filters.Search(name="title", label="Title", search_mode="contains", placeholder="Search for title"),
#         filters.Search(name="url", label="URL", search_mode="contains", placeholder="Search for URL"),
#     ]
#     fields = [
#         "id",
#         Field(name="title", label="Title", display=displays.Display(), input_=inputs.Text()),
#         Field(name="url", label="URL", display=displays.Display(), input_=inputs.Text()),
#         Field(name="owner_id", label="Owner ID", display=displays.Display(), input_=inputs.Text()),
#     ]
#
#
# # Admin model for UserGroupORM
# @app.register
# class UserGroupAdmin(Model):
#     label = "User Groups"
#     model = UserGroupORM
#     icon = "fas fa-user-friends"
#     page_pre_title = "user group list"
#     page_title = "user group model"
#     filters = [
#         filters.Search(name="group_id", label="Group ID", search_mode="contains", placeholder="Search for group ID"),
#         filters.Search(name="thing_id", label="Thing ID", search_mode="contains", placeholder="Search for thing ID"),
#         filters.Search(name="user_id", label="User ID", search_mode="contains", placeholder="Search for user ID"),
#     ]
#     fields = [
#         "id",
#         Field(name="group_id", label="Group ID", display=displays.Display(), input_=inputs.Text()),
#         Field(name="thing_id", label="Thing ID", display=displays.Display(), input_=inputs.Text()),
#         Field(name="user_id", label="User ID", display=displays.Display(), input_=inputs.Text()),
#     ]
