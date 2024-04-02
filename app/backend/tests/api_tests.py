import unittest
import requests

class AptTests(unittest.TestCase):
    def test_login_successful(self):
        data = {
            'username': 'user1',
            'password': '1'
        }

        response = requests.post('http://127.0.0.1:5000/api/v1.0/login', json=data)

        self.assertEqual(response.status_code, 200)

        self.assertEqual(response.json()['status'], 'success')

    def test_login_failure(self):
        data = {
            'username': 'nonexistent_user',
            'password': 'wrong_password'
        }

        response = requests.post('http://127.0.0.1:5000/api/v1.0/login', json=data)

        self.assertEqual(response.status_code, 401)

        self.assertEqual(response.json()['status'], 'error')
        self.assertEqual(response.json()['message'], 'Invalid username or password')

if __name__ == '__main__':
    unittest.main()
