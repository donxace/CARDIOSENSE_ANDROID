import matplotlib.pyplot as plt
import matplotlib.animation as animation
import numpy as np

# Example data (replace with your real RR intervals)
time = np.arange(0, 20, 0.5)
actual_rr = np.array([0.8, 0.82, 0.81, 0.79, 0.78, 0.80, 0.83, 0.82, 0.81, 0.80,
                      0.79, 0.81, 0.82, 0.83, 0.84, 0.82, 0.81, 0.80, 0.79, 0.78,
                      0.77, 0.78, 0.79, 0.80, 0.81, 0.82, 0.83, 0.82, 0.81, 0.80,
                      0.79, 0.78, 0.77, 0.76, 0.77, 0.78, 0.79, 0.80, 0.81, 0.82])
predicted_rr = actual_rr + np.random.normal(0, 0.01, len(actual_rr))

# Interpolate for smooth animation
smooth_time = np.linspace(time[0], time[-1], 500)
smooth_actual = np.interp(smooth_time, time, actual_rr)
smooth_predicted = np.interp(smooth_time, time, predicted_rr)

# Create figure
fig, ax = plt.subplots(figsize=(10,5))
line_actual, = ax.plot([], [], label='Actual RR', color='blue')
line_pred, = ax.plot([], [], label='Predicted RR', color='red')
ax.set_xlim(smooth_time[0], smooth_time[-1])
ax.set_ylim(min(min(actual_rr), min(predicted_rr)) - 0.05,
            max(max(actual_rr), max(predicted_rr)) + 0.05)
ax.set_xlabel('Time (s)')
ax.set_ylabel('RR Interval (s)')
ax.set_title('Smooth Animated RR Interval: Actual vs Predicted')
ax.legend()
ax.grid(True)

# Initialization function
def init():
    line_actual.set_data([], [])
    line_pred.set_data([], [])
    return line_actual, line_pred

# Animation function
def animate(i):
    line_actual.set_data(smooth_time[:i], smooth_actual[:i])
    line_pred.set_data(smooth_time[:i], smooth_predicted[:i])
    return line_actual, line_pred

# Animate
ani = animation.FuncAnimation(fig, animate, frames=len(smooth_time),
                              init_func=init, interval=10, blit=True)

plt.show()
