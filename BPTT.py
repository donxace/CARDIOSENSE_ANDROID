import numpy as np

# ---------- Activation functions ----------
def sigmoid(x): return 1 / (1 + np.exp(-x))
def dsigmoid(s): return s * (1 - s)
def dtanh(t): return 1 - t**2

# ---------- Parameters ----------
params = {}
for name in ['W_f', 'U_f', 'b_f',
             'W_i', 'U_i', 'b_i',
             'W_c', 'U_c', 'b_c',
             'W_o', 'U_o', 'b_o']:
    params[name] = float(input(f"Enter {name}: "))

# ---------- Initial states ----------
h_prev = float(input("Enter initial h_prev (h_{t-1}): "))
c_prev = float(input("Enter initial c_prev (c_{t-1}): "))

# ---------- Input sequence ----------
x_seq = [0.5, 0.6]  # fixed two timesteps
y = float(input("Enter target y: "))

# ---------- Forward pass ----------
forward_cache = []
print("\n========== Forward Pass ==========")
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

    forward_cache.append({'x': x, 'h_prev': h_prev, 'c_prev': c_prev,
                          'f': f, 'i': i, 'c_tilde': c_tilde, 'o': o,
                          'c': c, 'h': h})

    h_prev = h
    c_prev = c

    print(f"Timestep {t}: f={f:.6f}, i={i:.6f}, c_tilde={c_tilde:.6f}, o={o:.6f}, c={c:.6f}, h={h:.6f}")

# ---------- Loss ----------
h_last = forward_cache[-1]['h']
L = 0.5 * (y - h_last)**2
dL_dh_last = h_last - y
print(f"\nLoss = {L:.6f}, dL/dh_last = {dL_dh_last:.6f}\n")

# ---------- Backward pass ----------
grads_total = {k: 0.0 for k in params.keys()}
dh_next = 0.0
dc_next = 0.0
print("========== Backward Pass ==========")
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

    dh = (dL_dh_last if t == len(x_seq)-1 else 0) + dh_next
    dL_dc = dh * o * (1 - np.tanh(c)**2) + dc_next
    dL_df = dL_dc * c_prev
    dL_di = dL_dc * c_tilde
    dL_dc_tilde = dL_dc * i
    dL_do = dh * np.tanh(c)

    dz_f = dL_df * dsigmoid(f)
    dz_i = dL_di * dsigmoid(i)
    dz_c = dL_dc_tilde * dtanh(c_tilde)
    dz_o = dL_do * dsigmoid(o)

    grads_step = {
        'W_f': dz_f*x, 'U_f': dz_f*h_prev, 'b_f': dz_f,
        'W_i': dz_i*x, 'U_i': dz_i*h_prev, 'b_i': dz_i,
        'W_c': dz_c*x, 'U_c': dz_c*h_prev, 'b_c': dz_c,
        'W_o': dz_o*x, 'U_o': dz_o*h_prev, 'b_o': dz_o
    }

    print(f"\nTimestep {t+1} backward derivatives:")
    print(f" dh = {dh:.6f}")
    print(f" dL/dc = {dL_dc:.6f}, dL/df = {dL_df:.6f}, dL/di = {dL_di:.6f}, dL/dc_tilde = {dL_dc_tilde:.6f}, dL/do = {dL_do:.6f}")
    print(f" dz_f = {dz_f:.6f}, dz_i = {dz_i:.6f}, dz_c = {dz_c:.6f}, dz_o = {dz_o:.6f}")
    print(" Weight/bias gradients this timestep:")
    for k, v in grads_step.items():
        print(f"  {k} = {v:.6f}")

    for k, v in grads_step.items():
        grads_total[k] += v

    dh_next = dz_f*params['U_f'] + dz_i*params['U_i'] + dz_c*params['U_c'] + dz_o*params['U_o']
    dc_next = dL_dc * f

# ---------- Final accumulated gradients ----------
print("\n========== Final Accumulated Gradients ==========")
for k, v in grads_total.items():
    print(f"{k} = {v:.6f}")
