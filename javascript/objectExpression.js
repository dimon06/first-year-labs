"use strict"

const Const = (function () {
    function Const(value) {
        this.value = value
    }

    Const.prototype.evaluate = function () {
        return this.value
    }

    Const.prototype.toString = function () {
        return String(this.value)
    }

    Const.prototype.prefix = Const.prototype.toString

    Const.prototype.postfix = Const.prototype.toString
    //fixed
    Const.prototype.diff = function () {
        return ZERO
    } // :NOTE: new Const(0) в константу сохранить

    return Const;
})()

const Variable = (function () {
    function Variable(name) {
        this.name = name
    }

    Variable.prototype.evaluate = function (...args) {
        // fixed
        // :NOTE: args[vars.indexOf(this.name)]
        return args[vars.indexOf(this.name)];
    }

    Variable.prototype.toString = function () {
        return this.name
    }

    Variable.prototype.prefix = Variable.prototype.toString

    Variable.prototype.postfix = Variable.prototype.toString

    Variable.prototype.diff = function (variable) {
        // fixed
        return ((this.name === variable) ? ONE : ZERO) // :NOTE: лучше вынести константы ONE и ZERO, чтобы не создавать каждый раз объекты
    }

    return Variable
})()

const ZERO = new Const(0)
const ONE = new Const(1)
const THREE = new Const(3);
const INF = new Const(Infinity);

function Operations(...args) {
    this.args = args;
    // fixed
    // :NOTE: хранить в каждом объекте одного и того же типа operation, func и funcDiff плохо
    // :NOTE: это нужно хранить в прототипе конкретной операции, для которой будет прототипом Operations.prototype
}

Operations.prototype.diff = function (variable) {
    // :NOTE: вычисление производных можно реализовать, не пользуясь буквой, по которой мы считаем производную
    // :NOTE: на самом деле, производная это всегда сумма проведений исходных аргументов и производных аргументов
    // :NOTE: d(a * b) = da * b + db * a
    // :NOTE: d(a / b) = da / b - db * a / b^2
    // :NOTE: d(a + b) = da + db
    const elements = this.getKoefs();
    return elements.reduce((sum, elem, ind) =>
        new Add(sum, new Multiply(elem, this.args[ind].diff(variable))), ZERO);
}

Operations.prototype.evaluate = function (...args) {
    return this.constructor.func(...this.args.map(arg => arg.evaluate(...args)));
}

Operations.prototype.toString = function () {
    return this.args.map(arg => arg.toString()).join(" ") + " " + this.constructor.operation;
}

Operations.prototype.prefix = function () {
    return "(" + this.constructor.operation + " " + this.args.map(arg => arg.prefix()).join(" ") + ")";
}

Operations.prototype.postfix = function () {
    return "(" + this.args.map(arg => arg.postfix()).join(" ") + " " + this.constructor.operation + ")";
}

// fixed
// :NOTE: это фабрика конструкторов, а не конструктор, поэтому название с маленькой буквы
function makeOperations(operation, arity, func, koefs) {
    function Operate(...args) {
        Operations.call(this, ...args);
    }

    Operate.prototype = Object.create(Operations.prototype);
    Operate.prototype.constructor = Operate;
    Operate.operation = operation;
    Operate.arity = arity;
    Operate.func = func;
    Operate.prototype.getKoefs = koefs;
    return Operate;
}

const Add = makeOperations(
    "+", 2, (a, b) => a + b,
    function () {
        return [ONE, ONE];
    }
)

const Subtract = makeOperations(
    "-", 2, (a, b) => a - b,
    function () {
        return [ONE, new Negate(ONE)];
    }
)

const Multiply = makeOperations(
    "*", 2, (a, b) => a * b,
    function () {
        return [this.args[1], this.args[0]];
    }
)

const Divide = makeOperations(
    "/", 2, (a, b) => a / b,
    function () {
        return [new Divide(ONE, this.args[1]), new Divide(new Negate(this.args[0]), new Multiply(this.args[1], this.args[1]))];
    }
)

const Negate = makeOperations(
    "negate", 1, (a) => -a,
    function () {
        return [new Negate(ONE)];
    }
)

const createFunction = (begin, end, funct) =>
    Array.from({length: end - begin + 1}, (_, i) => funct(begin + i));

const Normal = (size) => {
    return makeOperations(
        "normal" + size, size,
        // fixed
        (...args) => { // :NOTE: let -> const
            let sum = args.reduce((sum, cur) => sum + (cur * cur / 2), 0);
            let fir = Math.pow(Math.E, -sum);
            let sec = Math.pow(2 * Math.PI, args.length / 2)
            return fir / sec;
        },
        function () {
            return this.args.map(arg => new Multiply(new Negate(arg), this));
        }
    )
}

const [Normal1, Normal2, Normal3, Normal4, Normal5] = createFunction(1, 5, Normal);

const Poly = (size) => {
    return makeOperations(
        "poly" + size, size,
        (x, ...args) => {
            // fixed
            // let answer = args.reduce(([sum, pow], cur) => [sum + pow * cur, pow * x], [0, 1])
            // return answer[0];
            return args.reduce(([sum, pow], cur) => [sum + pow * cur, pow * x], [0, 1])[0]
        },
        function () {
            let pow = ONE
            let ans = [ZERO];
            if (this.args.length > 1) ans[1] = ONE;
            for (let j = 2; j < this.args.length; j++) {
                ans[0] = new Add(ans[0], new Multiply(new Const(j - 1), new Multiply(this.args[j], pow)));
                pow = new Multiply(pow, this.args[0]);
                ans[j] = pow;
            }
            return ans;
        }
    )
}

const [Poly1, Poly2, Poly3, Poly4, Poly5] = createFunction(1, 5, Poly);

const sumCubs = (...args) => args.map(arg => arg * arg * arg).reduce((sum, cur) => sum + cur, 0)

const SumCb = makeOperations(
    "sumCb", INF, (...args) => sumCubs(...args),
    function () {
        return this.args.map(arg => new Multiply(new Multiply(arg, arg), THREE))
    }
)

const Rmc = makeOperations(
    "rmc", INF, (...args) => {
        return Math.cbrt(sumCubs(...args) / args.length);
    },
    function () {
        let res1 = new Multiply(new Multiply(THREE, new Multiply(this, this)), new Const(this.args.length))
        return this.args.map(arg => new Divide(new Multiply(new Multiply(arg, arg), THREE), res1))
    }
)

const CbMax = makeOperations(
    "cbMax", INF, (...args) => {
        return args[0] * args[0] * args[0] / sumCubs(...args);
    },
    function () {
        const first = this.args[0];
        const cubs = new SumCb(...this.args);

        const cubFirst = new Multiply(first, new Multiply(first, first));
        const squareCubs = new Multiply(cubs, cubs);

        let ans = this.args.map(arg => new Divide(new Negate(new Multiply(new Multiply(THREE, cubFirst), new Multiply(arg, arg))), squareCubs))
        ans[0] = new Divide(new Multiply(new Multiply(THREE, new Subtract(cubs, cubFirst)), new Multiply(first, first)), squareCubs);
        return ans;
    }
)

const vars = ['x', 'y', 'z']
// fixed
const operations = { // :NOTE: арность можно хранить как поле конструктора или прототипа
    '+': Add,
    '-': Subtract,
    '*': Multiply,
    '/': Divide,
    'negate': Negate,
    'sumCb': SumCb,
    'rmc': Rmc,
    'cbMax': CbMax
}

for (let i = 1; i <= 5; i++) {
    operations[`normal` + i] = Normal(i);
    operations['poly' + i] = Poly(i);
}

function MakeErrors(name) {
    let typeError
    switch (name) {
        case "FewArgs" :
            typeError = function FewArgs(message) {
                this.message = message;
                this.name = name;
            }
            break
        case "UnexpectedSymbol" :
            typeError = function UnexpectedSymbol(message) {
                this.message = message;
                this.name = name;
            }
            break
        case "ManyArgs" :
            typeError = function ManyArgs(message) {
                this.message = message;
                this.name = name;
            }
    }
    typeError.prototype = Object.create(Error.prototype);
    typeError.prototype.constructor = typeError;
    typeError.prototype.toString = function () {
        return this.name + ": " + this.message;
    };
    return typeError;
}

const FewArgsError = MakeErrors("FewArgs")

const UnexpectedSymbolError = MakeErrors("UnexpectedSymbol")

const ManyArgsError = MakeErrors("ManyArgs")

const parse = (str) => {
    let elements = [];
    for (let elem of str.split(" ").filter(arg => arg.length > 0)) {
        if (elem in operations) {
            let Operation = operations[elem];
            // fixed
            let args = elements.splice(-Operation.arity); // :NOTE: elements.splice(-argCount)
            elements.push(new Operation(...args));
        } else if (vars.includes(elem)) {
            elements.push(new Variable(elem));
        } // fixed
        else { // :NOTE: лучше считать константой все, что не переменная и операция
            elements.push(new Const(Number(elem)));
        }
    }
    return elements[0];
}

const parser = (type) => (str) => {
    str = parseString(type, str)

    let ind = 0

    const expectedBracket = (type ? '(' : ')')

    function getPosition() {
        if (type) {
            return str.length - ind - 1;
        }
        return ind;
    }

    function parseExp() {
        if (ind >= str.length) {
            throw new FewArgsError("Expected more elements in expression")
        } else if (str[ind] === '(') {
            if (!(str[++ind] in operations)) {
                throw new UnexpectedSymbolError("Expected operation, but find " + str[ind] + " in position: " + getPosition())
            }
            let Operation = operations[str[ind++]];
            let args = []
            while (ind < str.length && str[ind] !== ')') {
                args.push(parseExp())
            }
            if (ind >= str.length) {
                throw new FewArgsError("Expected more elements in expression")
            }
            if (Operation.arity !== INF) {
                if (Operation.arity < args.length) {
                    throw new ManyArgsError("Expected few arguments for operation" + " in position: " + getPosition())
                } else if (Operation.arity > args.length) {
                    throw new FewArgsError("Expected more arguments for operation" + " in position: " + getPosition())
                }
            }
            if (str[ind++] !== ')') {
                throw new UnexpectedSymbolError("Expected " + expectedBracket + ", but find " + str[--ind] + " in position: " + getPosition())
            }
            if (type) {
                args.reverse()
            }
            return new Operation(...args)
        } else if (!isNaN(str[ind])) {
            return new Const(Number(str[ind++]))
        } else if (vars.includes(str[ind])) {
            return new Variable(str[ind++])
        } else {
            throw new UnexpectedSymbolError("Unexpected symbol: " + str[ind] + " in position: " + getPosition())
        }
    }

    const answer = parseExp()
    if (ind < str.length) {
        throw new ManyArgsError("Can't parse end of the expression")
    }
    return answer;
}

const parsePrefix = parser(false)

const parsePostfix = parser(true)

const parseString = (type, str) => {
    let elements = []
    let elem = ""

    function update() {
        if (elem.length > 0) {
            elements.push(elem)
            elem = ""
        }
    }

    for (let i = 0; i < str.length; i++) {
        if (str[i] === '(' || str[i] === ')') {
            update()
            elements.push(str[i])
        } else if (str[i] === " ") {
            update()
        } else {
            elem += str[i]
        }
    }
    update()

    if (type) {
        // fixed
        elements.reverse() // :NOTE: recerce ллоает логику обработки и затрудняет обработку ошибок; хотя на самом деле type влияет только на порядок внутри скобок
        elements = elements.map(ch => {
            if (ch === '(') return ')';
            if (ch === ')') return '(';
            return ch;
        })
    }

    return elements
}