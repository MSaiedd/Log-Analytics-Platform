import requests
import random
import time
from datetime import datetime, timezone

URL = "http://localhost:8080/log"
SERVICE = "billing-service"

level_weights = {
    "INFO": 60,
    "WARN": 20,
    "ERROR": 18,
    "DEBUG": 2
}

messages = {
    "INFO": [
        "invoice generated successfully",
        "payment authorized",
        "refund processed",
        "subscription renewed",
        "billing cycle completed"
    ],
    "WARN": [
        "payment gateway latency high",
        "retrying failed payment",
        "invoice email delayed",
        "currency conversion fallback used"
    ],
    "ERROR": [
        "payment gateway timeout",
        "refund transaction failed",
        "invoice generation failed",
        "external billing provider unavailable"
    ],
    "DEBUG": [
        "payment request payload created",
        "billing callback parsed"
    ]
}

incident_mode = False
incident_cycles = 0

def weighted_level():
    global incident_mode, incident_cycles

    if incident_mode:
        incident_cycles -= 1
        if incident_cycles <= 0:
            incident_mode = False
        return random.choices(
            ["INFO", "WARN", "ERROR", "DEBUG"],
            weights=[20, 25, 50, 5],
            k=1
        )[0]

    if random.random() < 0.08:
        incident_mode = True
        incident_cycles = random.randint(5, 15)

    return random.choices(
        ["INFO", "WARN", "ERROR", "DEBUG"],
        weights=[60, 20, 18, 2],
        k=1
    )[0]

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

    sleep_time = random.uniform(1.5, 4.0)
    if incident_mode:
        sleep_time = random.uniform(0.3, 1.0)

    time.sleep(sleep_time)