from sqlalchemy import String, ForeignKey, DateTime
from sqlalchemy.orm import Mapped, mapped_column, relationship
from fastapi_users.db import SQLAlchemyBaseUserTable
from database import Base


class UserORM(SQLAlchemyBaseUserTable[int], Base):
    __tablename__ = "user"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    email: Mapped[str] = mapped_column(
        String(length=320), unique=True, index=True, nullable=False
    )
    username: Mapped[str] = mapped_column(
        String(length=320), unique=True, index=True, nullable=False
    )
    hashed_password: Mapped[str] = mapped_column(
        String(length=1024), nullable=False
    )

    user_groups = relationship("UserGroupORM", back_populates="user")
    groups = relationship("GroupORM", back_populates="owner")


class ThingORM(Base):
    __tablename__ = "thing"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    title: Mapped[str] = mapped_column(String(length=320), index=True, nullable=False)

    thing_parameters = relationship("ThingParameterORM", back_populates="thing", cascade="all, delete")
    user_groups = relationship("UserGroupORM", back_populates="thing", cascade="all, delete")


class ParameterORM(Base):
    __tablename__ = "parameter"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    key: Mapped[str] = mapped_column(String(length=320), index=True, nullable=False)
    value: Mapped[str] = mapped_column(String(length=320), index=True, nullable=False)

    thing_parameters = relationship("ThingParameterORM", back_populates="parameter", cascade="all, delete")


class ThingParameterORM(Base):
    __tablename__ = 'thing_parameter'

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    thing_id: Mapped[int] = mapped_column(ForeignKey('thing.id'))
    parameter_id: Mapped[int] = mapped_column(ForeignKey('parameter.id'))

    thing = relationship("ThingORM", back_populates="thing_parameters")
    parameter = relationship("ParameterORM", back_populates="thing_parameters")


class GroupORM(Base):
    __tablename__ = 'group'

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    title: Mapped[str] = mapped_column(String(length=320), nullable=False)
    url: Mapped[str] = mapped_column(String(length=320), unique=True)
    owner_id: Mapped[str] = mapped_column(ForeignKey("user.id"))

    user_groups = relationship("UserGroupORM", back_populates="group")
    owner = relationship("UserORM", back_populates="groups")


class UserGroupORM(Base):
    __tablename__ = 'user_group'

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    group_id: Mapped[int] = mapped_column(ForeignKey('group.id'))
    thing_id: Mapped[int] = mapped_column(ForeignKey('thing.id'))
    user_id: Mapped[int] = mapped_column(ForeignKey('user.id'))

    user = relationship("UserORM", back_populates="user_groups")
    group = relationship("GroupORM", back_populates="user_groups")
    thing = relationship("ThingORM", back_populates="user_groups")


# class NotificationORM(Base):
#     __tablename__ = "notification"
#
#     id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
#     user_id: Mapped[int] = mapped_column(ForeignKey('user.id'), nullable=False)
#     thing_id: Mapped[int] = mapped_column(ForeignKey('thing.id'), nullable=False)
#     datetime: Mapped[DateTime] = mapped_column(nullable=False)
#
#     user = relationship("UserORM", back_populates="notifications")
#     thing = relationship("ThingORM", back_populates="notifications")
