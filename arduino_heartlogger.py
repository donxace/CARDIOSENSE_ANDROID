# heart_logger.py
import serial
import sqlite3
import time
import re

# SQLite database
db_path = r"C:\Users\RYZEN 5 3400G\Desktop\Project\HeartPulse2\heart_data.db"
conn = sqlite3.connect(db_path)
cursor = conn.cursor()


def delete_table():
    tables = ["candidate_gate", "cell_state", "experiment", 
              "forget_gate", "heart_metrics", "hidden_state", 
              "input_gate", "lstm_input", "lstm_prediction", 
              "output_gate", "sqlite_sequence"]
    for table in tables:
        cursor.execute(f"DELETE FROM {table};")
    conn.commit()


def run_heart_logger():
    start = 1
    ser = serial.Serial('COM17', 9600, timeout=1)
    time.sleep(2)
    print("Listening for RR intervals and HRV...\n")

    delete_table()

    while True:
        line = ser.readline().decode('utf-8', errors='ignore').strip()
        if not line:
            continue

        # Stop if session summary appears
        if "===== SESSION SUMMARY =====" in line:
            print("\n📌 Session summary detected. Stopping logger.")
            break  # Exit the loop, but do NOT close connections

        # Check for RR Interval
        rr_match = re.search(r"RR Interval #\d+: (\d+) ms", line)
        if rr_match:
            rr = int(rr_match.group(1))
            bpm = 60000 / rr  # calculate BPM from RR interval
            print(f"💓 RR # {start}: {rr} ms | BPM: {bpm:.1f}")

            start += 1

            # Save BPM to SQLite
            cursor.execute(
                "INSERT INTO heart_metrics (Heart_Rate, rr_interval) VALUES (?, ?)", (int(bpm), rr)
            )
            conn.commit()



