# heart_logger.py
import serial
import sqlite3
import time
import re

def run_heart_logger():
    # SQLite database
    db_path = r"C:\Users\RYZEN 5 3400G\Desktop\Project\HeartPulse2\heart_data.db"
    conn = sqlite3.connect(db_path)
    cursor = conn.cursor()

    # Connect to Arduino
    ser = serial.Serial('COM17', 9600, timeout=1)
    time.sleep(2)
    print("Listening for RR intervals and HRV...\n")

    start = 1

    try:
        while True:
            line = ser.readline().decode('utf-8', errors='ignore').strip()
            if not line:
                continue

            # Check for RR Interval
            rr_match = re.search(r"RR Interval #\d+: (\d+) ms", line)
            if rr_match:
                rr = int(rr_match.group(1))
                bpm = 60000 / rr  # calculate BPM from RR interval
                print(f"💓 RR # {start}: {rr} ms | BPM: {bpm:.1f}")

                start = start + 1

                # Save BPM to SQLite
                cursor.execute("INSERT INTO heart_metrics (Heart_Rate, rr_interval) VALUES (?,?)", (int(bpm), rr))
                conn.commit()
                continue

            """ Check for HRV (RMSSD)
                hrv_match = re.search(r"HRV \(RMSSD\): (\d+\.?\d*) ms", line)
                if hrv_match:
                    hrv = float(hrv_match.group(1))
                    print(f"📊 HRV (RMSSD): {hrv:.2f} ms")

                 Save HRV to SQLite
                    cursor.execute("INSERT INTO heart_metrics (HRV_RMSSD) VALUES (?)", (hrv,))
                    conn.commit()
            """

    except KeyboardInterrupt:
        print("\nStopped logging.")
