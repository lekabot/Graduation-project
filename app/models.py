from sqlalchemy import create_engine, Column, Integer, String, Date, ForeignKey
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import relationship, sessionmaker

engine = create_engine("postgresql://localhost/Graduation-project")

Base = declarative_base()

class User(Base):
    __tablename__ = 'users'

    id = Column(Integer, primary_key=True)
    username = Column(String(250))
    password = Column(String(250))

class LogAction(Base):
    __tablename__ = 'log_actions'

    id = Column(Integer, primary_key=True)
    title = Column(String(250))

class AgreementType(Base):
    __tablename__ = 'agreement_types'

    id = Column(Integer, primary_key=True)
    title = Column(String(250))

class Country(Base):
    __tablename__ = 'countries'

    id = Column(Integer, primary_key=True)
    title = Column(String(250))

class Manufacturer(Base):
    __tablename__ = 'manufacturers'

    id = Column(Integer, primary_key=True)
    title = Column(String(250))
    business_form = Column(String(50))
    country_id = Column(Integer, ForeignKey('countries.id'))
    country = relationship("Country")

class EquipmentDocument(Base):
    __tablename__ = 'equipment_documents'

    id = Column(Integer, primary_key=True)
    agreement_type = Column(Integer, ForeignKey('agreement_types.id'))
    contract_number = Column(Integer)
    begin_date = Column(Date)

class Equipment(Base):
    __tablename__ = 'equipments'

    id = Column(Integer, primary_key=True)
    measured_parameters = Column(String(250))
    title = Column(String(250))
    manufacturer_id = Column(Integer, ForeignKey('manufacturers.id'))
    year_of_commissioning = Column(Date)
    measurement_range = Column(String(250))
    accuracy_class = Column(String(250))
    property_rights_info_id = Column(Integer, ForeignKey('equipment_documents.id'))
    installation_location = Column(String(250))

class Attestation(Base):
    __tablename__ = 'attestations'

    id = Column(Integer, primary_key=True)
    equipment = Column(Integer, ForeignKey('equipments.id'))
    validity_period = Column(Date)
    event_duration = Column(Date)

class Check(Base):
    __tablename__ = 'checks'

    id = Column(Integer, primary_key=True)
    equipment = Column(Integer, ForeignKey('equipments.id'))
    last_check = Column(Date)
    validity_period = Column(Date)
    planned_inspection = Column(Date)

class UserLog(Base):
    __tablename__ = 'user_log'

    id = Column(Integer, primary_key=True)
    user_id = Column(Integer, ForeignKey('users.id'))
    equipment_id = Column(Integer, ForeignKey('equipments.id'))
    action_id = Column(Integer, ForeignKey('log_actions.id'))
    log_date = Column(Date)

    user = relationship("User")
    equipment = relationship("Equipment")
    action = relationship("LogAction")


Session = sessionmaker(bind=engine)
session = Session()

# for equipment in session.query(Equipment).all():
#     print(equipment.id, equipment.title)