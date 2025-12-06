import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
from sklearn.ensemble import IsolationForest
import time
import random

# --- CONFIG ---
window_size = 10           # how many recent readings to analyze
delay = 0.5                # seconds between simulated readings
contamination = 0.15       # sensitivity (higher = more anomalies)
plot_points = 50           # number of points to show in graph

# --- Initialize ---
bpm_values = []
hrv_values = []
timestamps = []
model = IsolationForest(contamination=contamination, random_state=42)

# --- Live Plot Setup ---
plt.ion()
fig, ax = plt.subplots(2, 1, figsize=(8, 6))
fig.suptitle("💓 Real-Time AI Heart Monitor", fontsize=14)

# --- Simulation Function ---
def simulate_heart_data():
    """Generate random BPM/HRV with occasional anomalies"""
    base_bpm = random.randint(75, 85)
    base_hrv = random.randint(75, 90)
    
    # Random chance for anomaly (spike or drop)
    if random.random() < 0.15:
        base_bpm += random.randint(30, 50)   # spike
        base_hrv -= random.randint(30, 45)   # drop
    return base_bpm, max(20, base_hrv)

# --- Main Loop ---
while True:
    bpm, hrv = simulate_heart_data()
    bpm_values.append(bpm)
    hrv_values.append(hrv)
    timestamps.append(time.strftime("%H:%M:%S"))
    
    # Keep window size consistent
    if len(bpm_values) > window_size:
        bpm_values.pop(0)
        hrv_values.pop(0)
        timestamps.pop(0)
    
    # AI detection when enough data
    if len(bpm_values) >= 5:
        df = pd.DataFrame({"BPM": bpm_values, "HRV": hrv_values})
        model.fit(df)
        preds = model.predict(df)
        
        # Check if last value is anomalous
        if preds[-1] == -1:
            print(f"⚠️  Anomaly detected → BPM: {bpm}, HRV: {hrv}")
            if bpm > np.mean(df['BPM']) + 25:
                print("🫀 Sudden BPM spike detected!")
            if hrv < np.mean(df['HRV']) - 25:
                print("😰 Sudden HRV drop detected!")
        else:
            print(f"✅ Normal reading → BPM: {bpm}, HRV: {hrv}")
    
    # --- Plot live data ---
    ax[0].cla()
    ax[1].cla()
    
    ax[0].plot(bpm_values[-plot_points:], color='red', marker='o')
    ax[0].set_title("BPM (Beats Per Minute)")
    ax[0].set_ylim(50, 150)
    ax[0].grid(True, alpha=0.3)
    
    ax[1].plot(hrv_values[-plot_points:], color='blue', marker='o')
    ax[1].set_title("HRV (Heart Rate Variability)")
    ax[1].set_ylim(20, 120)
    ax[1].grid(True, alpha=0.3)
    
    plt.tight_layout()
    plt.pause(0.01)
    
    time.sleep(delay)
