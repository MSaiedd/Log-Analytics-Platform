import requests
import random
import time
from datetime import datetime, timezone

URL = "http://localhost:8080/log"
SERVICE = "auth-service"

level_weights = {
    "INFO": 70,
    "WARN": 15,
    "ERROR": 10,
    "DEBUG": 5
}

messages = {
    "INFO": [
        "user login successful",
        "session created",
        "token refreshed",
        "user logout successful",
        "password validation succeeded"
    ],
    "WARN": [
        "invalid password attempt",
        "multiple failed logins detected",
        "user account temporarily locked",
        "token nearing expiration"
    ],
    "ERROR": [
        "jwt token validation failed",
        "authentication provider timeout",
        "user session store unavailable",
        "login transaction failed"
    ],
    "DEBUG": [
        "authentication filter entered",
        "security context initialized",
        "jwt claims parsed",
        "session lookup completed"
    ]
}

def weighted_level():
    levels = list(level_weights.keys())
    weights = list(level_weights.values())
    return random.choices(levels, weights=weights, k=1)[0]

while True:
    level = weighted_level()
    payload = {
        "timestamp": datetime.now(timezone.utc).isoformat().replace("+00:00", "Z"),
        "service": SERVICE,
        "level": level,
        "message": random.choice(messages[level])
    }

    try:
        response = requests.post(URL, json=payload, timeout=5)
        print(response.status_code, payload)
    except Exception as e:
        print("ERROR:", e)

    sleep_time = random.uniform(0.5, 2.0)
    time.sleep(sleep_time)