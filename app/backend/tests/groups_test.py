import os
import sys

import httpx
import pytest
import pytest_asyncio

sys.path.append(os.path.join(sys.path[0], 'src'))

from src.main import app
from httpx import AsyncClient
from auth.manager import UserManager
from src.database import get_async_session
from sqlalchemy.ext.asyncio import AsyncSession
from fastapi import Depends


@pytest.fixture
def anyio_backend():
    return 'asyncio'


@pytest_asyncio.fixture()
async def client():
    async with AsyncClient(app=app, base_url="http://127.0.0.1") as client:
        yield client

# create_empty_endpoint, delete_by_username, get_group_by_user_id have been tested in user register
