import os
import time
import keyboard  # pip install keyboard

window = ['-'] * 6    # 6-character baseline
pulse = list("|/")    # heartbeat pulse
delay = 0.2
active_pulses = []    # list to track pulses currently moving

while True:
    # Check if 'b' is pressed
    if keyboard.is_pressed('b'):
        # Add a new pulse starting at the rightb
        active_pulses.append(0)
        time.sleep(0.2)  # small debounce so a single press adds one pulse

    # Create the current line
    line = window.copy()

    # Move each active pulse
    new_active = []
    for step in active_pulses:
        for i in range(len(pulse)):
            pos = step - i
            if 0 <= pos < len(line):
                line[pos] = pulse[i]
        if step < len(window) + len(pulse) - 1:
            new_active.append(step + 1)  # move pulse to next step
    active_pulses = new_active

    os.system('cls' if os.name == 'nt' else 'clear')
    print(''.join(line))
    time.sleep(delay)
