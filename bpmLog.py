import serial
import sqlite3
import time
import os

# SQLite database file


# Connect to database (creates if not exists)
conn = sqlite3.connect(r"C:\Users\RYZEN 5 3400G\Desktop\Project\HeartPulse2\heartbeat\heart_data.db")
cursor = conn.cursor()

# Create table if not exists
# Columns: id, experiment_id, description, bpm, timestamp
cursor.execute('''
CREATE TABLE IF NOT EXISTS bpm_log (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    experiment_id INTEGER,
    description TEXT,
    bpm INTEGER,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP
)
''')
conn.commit()

# Connect to Arduino
arduino = serial.Serial('COM17', 9600, timeout=1)
time.sleep(2)  # wait for Arduino reset

# Experiment info
experiment_id = 1
description = "Heart rate experiment"

print("Logging BPM data to SQLite...\n")

try:
    while True:
        line = arduino.readline().decode('utf-8', errors='ignore').strip()
        if line.isdigit():
            bpm = int(line)
            bpm_scaled = bpm * 12  # scale if needed

            # Insert into database
            cursor.execute(
                "INSERT INTO bpm_log (experiment_id, description, bpm) VALUES (?, ?, ?)",
                (experiment_id, description, bpm_scaled)
            )
            conn.commit()

            print(f"Saved: {bpm_scaled}")
except KeyboardInterrupt:
    print("\nStopped logging.")
finally:
    arduino.close()
    conn.close()
