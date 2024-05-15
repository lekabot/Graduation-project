from sqlalchemy import (TIMESTAMP, Boolean, String, ForeignKey)
from sqlalchemy.orm import relationship
from sqlalchemy.orm import Mapped, mapped_column
from database import Base


class ThingORM(Base):
    __tablename__ = "thing"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    title: Mapped[str] = mapped_column(String(length=320), index=True, nullable=False)

    thing_parameter = relationship("ThingParameterORM", back_populates="thing")


class ParameterORM(Base):
    __tablename__ = "parameter"

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    key: Mapped[str] = mapped_column(String(length=320), index=True, nullable=False)
    value: Mapped[str] = mapped_column(String(length=320), index=True, nullable=False)

    thing_parameter = relationship("ThingParameterORM", back_populates="parameter")


class ThingParameterORM(Base):
    __tablename__ = 'thing_parameter'

    id: Mapped[int] = mapped_column(primary_key=True, autoincrement=True)
    thing_id: Mapped[int] = mapped_column(ForeignKey('thing.id'))
    parameter_id: Mapped[int] = mapped_column(ForeignKey('parameter.id'))

    thing = relationship("ThingORM", back_populates="thing_parameter")
    parameter = relationship("ParameterORM", back_populates="thing_parameter")
