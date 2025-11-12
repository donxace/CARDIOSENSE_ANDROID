import numpy as np

# ---------- Activation functions ----------
def sigmoid(x):
    return 1 / (1 + np.exp(-x))

def dsigmoid(s):
    return s * (1 - s)

def dtanh(t):
    return 1 - t**2

# ---------- Input sequence and target ----------
x_seq = [2, 4]
y = 0.8

# ---------- Weights and biases ----------
params = {
    'W_f': 0.4, 'U_f': 0.3, 'b_f': 0.1,
    'W_i': 0.5, 'U_i': 0.1, 'b_i': 0.0,
    'W_c': 0.6, 'U_c': 0.2, 'b_c': 0.0,
    'W_o': 0.7, 'U_o': 0.1, 'b_o': 0.0
}

# ---------- Initial states ----------
h_prev = 0.0
c_prev = 0.0

# ---------- Forward pass ----------
forward_cache = []
for t, x in enumerate(x_seq, 1):
    z_f = params['W_f']*x + params['U_f']*h_prev + params['b_f']
    z_i = params['W_i']*x + params['U_i']*h_prev + params['b_i']
    z_c = params['W_c']*x + params['U_c']*h_prev + params['b_c']
    z_o = params['W_o']*x + params['U_o']*h_prev + params['b_o']

    f = sigmoid(z_f)
    i = sigmoid(z_i)
    c_tilde = np.tanh(z_c)
    o = sigmoid(z_o)

    c = f * c_prev + i * c_tilde
    h = o * np.tanh(c)

    forward_cache.append({
        'x': x, 'h_prev': h_prev, 'c_prev': c_prev,
        'f': f, 'i': i, 'c_tilde': c_tilde, 'o': o,
        'c': c, 'h': h
    })

    h_prev = h
    c_prev = c

    print(f"Timestep {t}: f={f:.6f}, i={i:.6f}, c_tilde={c_tilde:.6f}, o={o:.6f}, c={c:.6f}, h={h:.6f}")

# ---------- Loss ----------
h_last = forward_cache[-1]['h']
L = 0.5 * (y - h_last)**2
dL_dh_last = h_last - y
print(f"\nLoss = {L:.6f}, dL/dh_last = {dL_dh_last:.6f}\n")

# ---------- Backward pass ----------
grads_total = {key: 0.0 for key in params.keys()}
dh_next = 0.0
dc_next = 0.0

for t in reversed(range(len(x_seq))):
    cache = forward_cache[t]
    x = cache['x']
    h_prev = cache['h_prev']
    c_prev = cache['c_prev']
    f = cache['f']
    i = cache['i']
    c_tilde = cache['c_tilde']
    o = cache['o']
    c = cache['c']
    h = cache['h']

    # Compute dh for this timestep (includes next timestep contribution)
    if t == len(x_seq) - 1:
        dh = dL_dh_last + dh_next
    else:
        dh = dh_next

    # Compute dc (cell derivative)
    dL_dc = dh * o * (1 - np.tanh(c)**2) + dc_next
    dL_df = dL_dc * c_prev
    dL_di = dL_dc * c_tilde
    dL_dc_tilde = dL_dc * i
    dL_do = dh * np.tanh(c)

    print(f"Timestep {t+1} derivatives:")
    print(f"  dL/dh = {dh:.6f}")
    print(f"  dL/dc = {dL_dc:.6f}, dL/df = {dL_df:.6f}, dL/di = {dL_di:.6f}, dL/dc_tilde = {dL_dc_tilde:.6f}, dL/do = {dL_do:.6f}")

    # Pre-activation derivatives
    dz_f = dL_df * dsigmoid(f)
    dz_i = dL_di * dsigmoid(i)
    dz_c = dL_dc_tilde * dtanh(c_tilde)
    dz_o = dL_do * dsigmoid(o)

    # Weight and bias gradients per timestep
    grads_step = {
        'W_f': dz_f * x,
        'U_f': dz_f * h_prev,
        'b_f': dz_f,
        'W_i': dz_i * x,
        'U_i': dz_i * h_prev,
        'b_i': dz_i,
        'W_c': dz_c * x,
        'U_c': dz_c * h_prev,
        'b_c': dz_c,
        'W_o': dz_o * x,
        'U_o': dz_o * h_prev,
        'b_o': dz_o
    }

    print("\n  Per-timestep weight/bias gradients:")
    for key, val in grads_step.items():
        print(f"    {key} = {val:.6f}")

    # Accumulate total gradients
    for key in grads_total.keys():
        grads_total[key] += grads_step[key]

    # Prepare for next timestep
    dh_next = (
        dz_f * params['U_f'] +
        dz_i * params['U_i'] +
        dz_c * params['U_c'] +
        dz_o * params['U_o']
    )
    dc_next = dL_dc * f

    print(f"  dh_next = {dh_next:.6f}, dc_next = {dc_next:.6f}\n")

# ---------- Final accumulated gradients ----------
print("========== Final Accumulated Gradients ==========")
for key, val in grads_total.items():
    print(f"{key} = {val:.6f}")
