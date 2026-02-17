#!/usr/bin/env python3
"""
Simple test script to verify SocialApp Server is running and responding
"""

import http.client
import json

def test_server():
    try:
        # Connect to server
        conn = http.client.HTTPConnection("127.0.0.1", 8080, timeout=5)
        
        # Test 1: List users
        print("Testing: GET /api/users/list")
        conn.request("GET", "/api/users/list")
        response = conn.getresponse()
        data = json.loads(response.read().decode())
        print(f"✓ Status: {response.status}")
        print(f"✓ Response: {json.dumps(data, indent=2)}")
        print()
        
        # Test 2: Register a user
        print("Testing: POST /api/users/register")
        user_data = json.dumps({"username": "testuser", "password": "testpass"})
        conn = http.client.HTTPConnection("127.0.0.1", 8080, timeout=5)
        conn.request("POST", "/api/users/register", user_data, {"Content-Type": "application/json"})
        response = conn.getresponse()
        data = json.loads(response.read().decode())
        print(f"✓ Status: {response.status}")
        print(f"✓ Response: {json.dumps(data, indent=2)}")
        print()
        
        # Test 3: Login
        print("Testing: POST /api/users/login")
        login_data = json.dumps({"username": "testuser", "password": "testpass"})
        conn = http.client.HTTPConnection("127.0.0.1", 8080, timeout=5)
        conn.request("POST", "/api/users/login", login_data, {"Content-Type": "application/json"})
        response = conn.getresponse()
        data = json.loads(response.read().decode())
        print(f"✓ Status: {response.status}")
        print(f"✓ Success: {data.get('success')}")
        print()
        
        print("✓ All tests passed! Server is working correctly.")
        
    except Exception as e:
        print(f"✗ Error: {e}")
        print("Make sure the server is running: python SocialAppServer.py")
        return False
    
    return True

if __name__ == "__main__":
    test_server()
