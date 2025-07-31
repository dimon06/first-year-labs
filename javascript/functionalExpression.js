"use strict"

const operation = (f) => (...args) => (...vars) =>
    f(...args.map(arg => arg(...vars)))

const add = operation((a, b) => a + b)

const subtract = operation((a, b) => a - b)

const multiply = operation((a, b) => a * b)

const divide = operation((a, b) => a / b)

const negate = operation((a) => -a)

const lessGreaterOperation = (f) => (...args) => (...vars) =>
    args.map((funct, ind) => ind === 0 ? true : f(args[ind - 1](...vars), funct(...vars)))
        .every(result => result) ? 1 : 0;


const less3 = lessGreaterOperation((a, b) => a < b)

const greater4 = lessGreaterOperation((a, b) => a > b)

const lessEq2 = lessGreaterOperation((a, b) => a <= b)

const greaterEq5 = lessGreaterOperation((a, b) => a >= b)

const cnst = (value) => () => value

const getVariable = (ind) => (...args) => args[ind]

const varInd = {
    'x': getVariable(0),
    'y': getVariable(1),
    'z': getVariable(2),
    't': getVariable(3)
}

const variable = (name) => varInd[name]

const operations = {
    '+': [add, 2],
    '-': [subtract, 2],
    '*': [multiply, 2],
    '/': [divide, 2],
    'negate': [negate, 1],
    'less3': [less3, 3],
    'greater4': [greater4, 4],
    'lessEq2': [lessEq2, 2],
    'greaterEq5': [greaterEq5, 5]
}

const phi = cnst((1 + Math.sqrt(5)) / 2)

const tau = cnst(2 * Math.PI)

const constants = {
    'phi': phi,
    'tau': tau
}

const parse = (str) => (...vals) => {
    let elements = [];
    for (let elem of str.split(" ").filter(arg => arg.length > 0)) {
        if (elem in operations) {
            let operate = operations[elem]
            let arr = []
            for (let i = 0; i < operate[1]; i++) {
                arr.push(elements.pop());
            }
            arr.reverse()
            elements.push(operate[0](...arr))
        } else if ((elem[0] >= '0' && elem[0] <= '9') || elem[0] === '-') {
            elements.push(cnst(Number(elem)))
        } else if (elem in varInd) {
            elements.push(variable(elem))
        } else if (elem in constants) {
            elements.push(constants[elem])
        }
    }
    return elements[0](...vals)
}

/*
let expression = subtract(
    multiply(
        variable("x"),
        variable("x")
    ),
    add(
        multiply(
            cnst(2),
            variable("x")
        ),
        cnst(1)
    )
);

for (let x = 0; x <= 10; x++) {
    console.log(expression(x))
}
*/