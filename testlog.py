import serial
import os
import time
import sqlite3
import re
from datetime import datetime

# -------------------------
# SQLite setup
# -------------------------
db_path = r"C:\Users\RYZEN 5 3400G\Desktop\Project\HeartPulse2\heart_data.db"
conn = sqlite3.connect(db_path)
cursor = conn.cursor()

# Create table if not exists
cursor.execute("""
CREATE TABLE IF NOT EXISTS heart_metrics (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    timestamp DATETIME DEFAULT CURRENT_TIMESTAMP,
    Heart_Rate INTEGER,
    HRV_RMSSD REAL
)
""")
conn.commit()

# -------------------------
# Connect to Arduino
# -------------------------
ser = serial.Serial('COM17', 9600, timeout=1)
time.sleep(2)  # allow Arduino to boot

# -------------------------
# Counters
# -------------------------
rr_no = 1
hrv_no = 1

# -------------------------
# Clear terminal function
# -------------------------
def clear_terminal():
    if os.name == 'nt':
        os.system('cls')  # Windows
    else:
        os.system('clear')  # Mac/Linux

print("Listening to Arduino...\n")

# -------------------------
# Main loop
# -------------------------
try:
    while True:
        line = ser.readline().decode('utf-8', errors='ignore').strip()
        if not line:
            continue

        # Detect Arduino startup/reset message
        if "Starting Heart Monitor" in line or "Arduino R4 ready" in line:
            clear_terminal()
            print("🔄 Arduino Reset Detected. Terminal cleared!\n")
            rr_no = 1   # reset counters
            hrv_no = 1

        # --- Parse RR Interval ---
        rr_match = re.search(r"RR Interval #\d+: (\d+) ms", line)
        if rr_match:
            rr_interval = int(rr_match.group(1))
            timestamp = datetime.now()
            cursor.execute(
                "INSERT INTO heart_metrics (Heart_Rate, timestamp) VALUES (?, ?)",
                (rr_interval, timestamp)
            )
            conn.commit()
            print(f"💓 RR Interval # {rr_no}: {rr_interval} ms | Stored in Heart_Rate ✅")
            rr_no += 1

        # --- Parse HRV (RMSSD) ---
        hrv_match = re.search(r"HRV.*?([\d\.]+)", line)
        if hrv_match:
            hrv_str = hrv_match.group(1)
            try:
                hrv = float(hrv_str)
            except ValueError:
                continue  # skip invalid lines
            timestamp = datetime.now()
            cursor.execute(
                "UPDATE heart_metrics SET HRV_RMSSD = ? WHERE id = (SELECT MAX(id) FROM heart_metrics)",
                (hrv,)
            )
            conn.commit()
            print(f"📊 HRV (RMSSD) # {hrv_no}: {hrv:.2f} ms | Stored in HRV_RMSSD ✅")
            hrv_no += 1

except KeyboardInterrupt:
    print("\nStopped logging.")

finally:
    ser.close()
    conn.close()
