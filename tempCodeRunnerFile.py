import serial
import sqlite3
import time

# SQLite database path
db_path = r"C:\Users\RYZEN 5 3400G\Desktop\Project\HeartPulse2\heartbeat\heart_data.db"

# Connect to database
conn = sqlite3.connect(db_path)
cursor = conn.cursor()

# Connect to Arduino
ser = serial.Serial('COM17', 115200, timeout=1)
time.sleep(2)

print("Listening for heartbeats...")

try:
    while True:
        line = ser.readline().decode('utf-8', errors='ignore').strip()

        if line.startswith("BEAT"):
            parts = line.split()

            if len(parts) == 2 and parts[1].isdigit():
                bpm = int(parts[1])

                # Insert into SQLite
                cursor.execute(
                    "INSERT INTO heart_metrics (Heart_Rate) VALUES (?)",
                    (bpm,)
                )
                conn.commit()

                print(f"💓 Beat Detected | BPM: {bpm} | Saved to DB ✅")

except KeyboardInterrupt:
    print("\nStopped.")
finally:
    ser.close()
    conn.close()
