import serial
import csv
import time
import os

# CSV file name
file_name = 'bpm_log.csv'

# Create CSV file with header if it doesn't exist
if not os.path.exists(file_name):
    with open(file_name, 'w', newline='') as file:
        writer = csv.writer(file)
        writer.writerow(['BPM'])

# Connect to Arduino
arduino = serial.Serial('COM18', 9600, timeout=1)
time.sleep(2)  # wait for Arduino to reset

print("Logging BPM data...\n")

# Open CSV in append mode and force flushing
with open(file_name, 'a', newline='', buffering=1) as file:
    writer = csv.writer(file)
    try:
        while True:
            line = arduino.readline().decode('utf-8', errors='ignore').strip()
            if line.isdigit():  # only log numeric BPM
                bpm = int(line)
                writer.writerow([bpm*12])
                file.flush()  # force writing to disk
                print(f"Saved: {bpm*12}", flush=True)
    except KeyboardInterrupt:
        print("\nStopped logging.")
        arduino.close()
