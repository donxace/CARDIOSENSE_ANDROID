import numpy as np

def sigmoid(x):
    sigm = 1/(1+np.exp(-x))

    return sigm

x_t = np.array([0.8])
h_tPrev = np.array([0.160])
c_tPrev = np.array([0.272])

##forget gate variables
w_f= np.array([0.5])
u_f= np.array([0.3])
b_f = np.array([0.1])

##input gate variables
w_i = np.array([0.4])
u_i = np.array([0.2])
b_i = np.array([0.1])

##output gate variables
w_o = np.array([0.4])
u_o = np.array([0.2])
b_o = np.array([0.1])

##candidate gate variables
w_ca = np.array([0.6])
u_ca = np.array([0.4])
b_ca = np.array([0.0])



def forgetGate(x_t, h_tPrev, w_f, u_f, b_f):
    forgetG = sigmoid(np.dot(x_t, w_f) + np.dot(u_f, h_tPrev) + b_f)

    return forgetG

f_t = forgetGate(x_t, h_tPrev, w_f, u_f, b_f)

def inputGate(x_t, h_tPrev, w_i, u_i, b_i):
    inputG = sigmoid(np.dot(x_t, w_i) + np.dot(u_i, h_tPrev) + b_i)

    return inputG

i_t = inputGate(x_t, h_tPrev, w_i, u_i, b_i)

def outputGate(x_t, h_tPrev, w_o, u_o, b_o):
    outputG = sigmoid(np.dot(x_t, w_o) + np.dot(u_o, h_tPrev) + b_o)

    return outputG

o_t = outputGate(x_t, h_tPrev, w_o, u_o, b_o)

def candidateState(x_t, h_tPrev, w_ca, u_ca, b_ca):
    candidate = np.tanh(np.dot(x_t, w_ca) + np.dot(u_ca, h_tPrev) + b_ca)

    return candidate

ca_t = candidateState(x_t, h_tPrev, w_ca, u_ca, b_ca)

def cellState(f_t, c_tPrev, i_t, ca_t):
    cell = np.array(np.dot(f_t, c_tPrev) + np.dot(i_t, ca_t))

    return cell

c_t = cellState(f_t, c_tPrev, i_t, ca_t)

def hiddenState(o_t, c_t):
    hidden = o_t * np.tanh(c_t)

    return hidden

h_t = hiddenState(o_t, c_t)

print("Forget Gate: ", f_t)
print("Input Gate: ", i_t)
print("Output Gate: ", o_t)
print("Candidate Gate: ", ca_t)
print("Cell State: ", c_t)
print("Hidden State: ", h_t)
