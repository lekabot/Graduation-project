import os
import sys

from fastapi.testclient import TestClient
from fastapi import Response

sys.path.append(os.path.join(sys.path[0], 'src'))

from src.main import app

client = TestClient(app)


def test_logout():
    response_login = client.post("/auth/login", json={"username": "1", "password": "1"})
    assert response_login.status_code >= 200
    response_logout = client.post("/auth/logout")
    assert response_logout.status_code >= 200


def test_authentication():
    response = client.post("/auth/login", json={"username": "1", "password": "1"})
    assert response.status_code >= 200


def test_registration():
    response = client.post("/auth/register",
                           json={
                               "email": "string",
                               "password": "string",
                               "is_active": True,
                               "is_superuser": False,
                               "is_verified": False,
                               "username": "string"
                           })

    assert response.status_code >= 200
    assert "id" in response.json()


def test_delete_by_username_link_user_found():
    username = "string"
    response = client.delete(f"/user/delete_by_username/{username}")
    assert response.status_code == 204


def test_delete_by_username_link_user_not_found():
    username = "333"
    response = client.delete(f"/user/delete_by_username/{username}")
    assert response.status_code == 404


def test_get_by_id_link():
    user_id = 1
    response = client.get(f"/user/get_by_id/{user_id}")
    assert response.status_code == 200


def test_get_by_username():
    username = 1
    response = client.get(f"/user/get_by_id/{username}")
    assert response.status_code == 200
