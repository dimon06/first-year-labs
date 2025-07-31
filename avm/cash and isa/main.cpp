#include "iostream"
#include "string"
#include "map"
#include "vector"
#include "algorithm"
#include "cstdint"
#include "cstring"

using namespace std;

const int MEM_SIZE = 512*1024;
const int ADDR_LEN = 19;
const int CACHE_WAY = 4;
const int CACHE_TAG_LEN = 10;
const int CACHE_INDEX_LEN = 4;
const int CACHE_OFFSET_LEN = 5;
const int CACHE_SIZE = 2048;
const int CACHE_LINE_SIZE = 32;
const int CACHE_LINE_COUNT = 64;
const int CACHE_SETS = 16;
int PROGRAM_COUNTER = 0x10000;
const int REGISTERS_LEN = 32;
int TIME = 0;

vector<int>REGISTERS(REGISTERS_LEN, 0);

vector<int>memory(MEM_SIZE);

vector<int> cache_hits_lru = {0, 0}, cache_hits_plru = {0, 0};
vector<int> cnt_lru = {0, 0}, cnt_plru = {0, 0};

struct information {
    int tag, index, offset;
    information() {
        tag = index = offset = 0;
    }
    information(int x, int y, int z) {
        tag = x;
        index = y;
        offset = z;
    }
};

information int_to_information(int val) {
    return information((val >> (CACHE_INDEX_LEN+CACHE_OFFSET_LEN)), (val >> CACHE_OFFSET_LEN)%(1 << CACHE_INDEX_LEN), val%(1 << CACHE_OFFSET_LEN));
}

int information_to_int(information inf) {
    return (inf.tag << (CACHE_INDEX_LEN+CACHE_OFFSET_LEN)) + (inf.index << CACHE_OFFSET_LEN) + inf.offset;
}

struct cache_line {
    information inf;
    int timer;
    bool dirty;
    cache_line() {
        inf = information();
        dirty = false;
        timer = 0;
    }
    cache_line(information _inf, int time) {
        inf = _inf;
        dirty = false;
        timer = time;
    }
    cache_line(information _inf, int time, bool dir) {
        inf = _inf;
        dirty = dir;
        timer = time;
    }
};

struct cache_block {
    vector<cache_line>block;
    int cnt_used;
    cache_block() {
        block.assign(CACHE_WAY, cache_line());
        cnt_used = 0;
    }

    void update_lru(information inf, int type, bool dir) {
        for (int i = 0; i < CACHE_WAY; ++i) {
            if (block[i].inf.tag  == inf.tag && block[i].timer > 0) {
                cache_hits_lru[type]++;
                block[i].dirty = (block[i].dirty | dir);
                return;
            }
        }
        int line_num = 0;
        for (int i = 0; i < CACHE_WAY; ++i) {
            if (block[i].timer < block[line_num].timer) {
                line_num = i;
            }
        }
        int ind = information_to_int(inf);
        block[line_num] = cache_line(inf, ++TIME, dir);
    }

    void update_plru(information inf, int type, bool dir) {
        for (int i = 0; i < CACHE_WAY; ++i) {
            if (block[i].inf.tag  == inf.tag && block[i].timer > 0) {
                cache_hits_plru[type]++;
                block[i].dirty = (block[i].dirty | dir);
                return;
            }
        }
        int zn = -1;
        for (int i = 0; i < CACHE_WAY; ++i) {
            if (!block[i].timer) {
                block[i] = cache_line(inf, 1, dir);
                cnt_used++;
                zn = i;
                break;
            }
        }
        if (cnt_used == CACHE_WAY) {
            cnt_used = 1;
            for (int i = 0; i < CACHE_WAY; ++i) {
                if (i != zn) {
                    block[i] = cache_line(information(), 0);
                }
            }
        }
    }
};

struct cache_memory {
    vector<cache_block>cache;
    cache_memory() {
        cache.assign(CACHE_SETS, cache_block());
    }

    void update_lru(information inf, int type, bool dir) {
        cnt_lru[type]++;
        cache[inf.index].update_lru(inf, type, dir);
    }

    void update_plru(information inf, int type, bool dir) {
        cnt_plru[type]++;
        cache[inf.index].update_plru(inf, type, dir);
    }

};

cache_memory cache_lru = cache_memory(), cache_plru = cache_memory();

map<string, string>registers = {{"zero", "00000"}, {"ra", "00001"},
                                {"sp", "00010"}, {"gp", "00011"},
                                {"tp", "00100"}, {"t0", "00101"},
                                {"t1", "00110"}, {"t2", "00111"},
                                {"s0", "01000"}, {"fp", "01000"},
                                {"s1", "01001"}, {"a0", "01010"},
                                {"a1", "01011"}, {"a2", "01100"},
                                {"a3", "01101"}, {"a4", "01110"},
                                {"a5", "01111"}, {"a6", "10000"},
                                {"a7", "10001"}, {"s2", "10010"},
                                {"s3", "10011"}, {"s4", "10100"},
                                {"s5", "10101"}, {"s6", "10110"},
                                {"s7", "10111"}, {"s8", "11000"},
                                {"s9", "11001"}, {"s10", "11010"},
                                {"s11", "11011"}, {"t3", "11100"},
                                {"t4", "11101"}, {"t5", "11110"},
                                {"t6", "11111"}};

string get_register(string val) {
    if (registers.find(val) != registers.end()) {
        return registers[val];
    }
    cerr << "Not a register value(:";
    exit(1);
}

int get_int_register(string val) {
    string res = get_register(val);
    int ans = 0, mn = 1;
    for (int i = res.size()-1; i >= 0; --i) {
        ans+=(res[i]-'0')*mn;
        mn<<=1;
    }
    return ans;
}

map<string, string>funct3 = {{"jalr", "000"}, {"beq", "000"}, {"bne", "001"}, {"blt", "100"},
                             {"bge", "101"}, {"bltu", "110"}, {"bgeu", "111"}, {"lb", "000"},
                             {"lh", "001"}, {"lw", "010"}, {"lbu", "100"}, {"lhu", "101"},
                             {"sb", "000"}, {"sh", "001"}, {"sw", "010"}, {"addi", "000"},
                             {"slti", "010"}, {"sltiu", "011"}, {"xori", "100"}, {"ori", "110"},
                             {"andi", "111"}, {"slli", "001"}, {"srli", "101"}, {"srai", "101"},
                             {"add", "000"}, {"sub", "000"}, {"sll", "001"}, {"slt", "010"},
                             {"sltu", "011"}, {"xor", "100"}, {"srl", "101"}, {"sra", "101"},
                             {"or", "110"}, {"and", "111"}, {"fence", "000"}, {"fence.i", "001"},
                             {"ecall", "000"}, {"ebreak", "000"}, {"mul", "000"}, {"mulh", "001"},
                             {"mulhsu", "010"}, {"mulhu", "011"}, {"div", "100"}, {"divu", "101"},
                             {"rem", "110"}, {"remu", "111"}};

char get_type(string name) {
    if (name == "slli" || name == "srli" || name == "srai" || name == "add"
        || name == "sub" || name == "sll" || name == "slt" || name == "sltu"
        || name == "xor" || name == "srl" || name == "sra" || name == "or"
        || name == "and" || name == "mul" || name == "mulh" || name == "mulhsu"
        || name == "mulhu" || name == "div" || name == "divu" || name == "rem" || name == "remu") {
        return 'r';
    } else if (name == "jalr" || name == "lb" || name == "lh" || name == "lw"
               || name == "lbu" || name == "lhu" || name == "addi" || name == "slti"
               || name == "sltiu" || name == "xori" || name == "ori" || name == "andi") {
        return 'i';
    } else if (name == "sb" || name == "sh" || name == "sw") {
        return 's';
    } else if (name == "beq" || name == "bne" || name == "blt" || name == "bge"
               || name == "bltu" || name == "bgeu") {
        return 'b';
    } else if (name == "lui" || name == "auipc") {
        return 'u';
    } else if (name == "jal") {
        return 'j';
    } else if (name == "fence" || name == "fence.i") {
        return 'f';
    } else if (name == "ecall" || name == "ebreak") {
        return 'e';
    }
    return 'z';
}

string mashine_kode(vector<string>args) {
    string operation = args[0];
    char type = get_type(operation);
    if (operation == "jalr" || operation == "lb" || operation == "lh" || operation == "lw" || operation == "lbu" || operation == "lhu") {
        swap(args[2], args[3]);
    } else if (type == 's') {
        swap(args[1], args[2]);
        swap(args[1], args[3]);
    }

    if ((type != 'r' && type != 'f' && type != 'e') || operation == "slli" || operation == "srli" || operation == "srai") {
        string bin = "";
        if (args.back().size() >= 2 && args.back().substr(0, 2) == "0x") {
            for (int i = 2; i < args.back().size(); ++i) {
                int x;
                if (args.back()[i] <= '9') {
                    x = args.back()[i]-'0';
                } else {
                    x = 10+args.back()[i]-'a';
                }
                string temp = "";
                while (x > 0) {
                    temp+=char('0'+(x%2));
                    x>>=1;
                }
                while (temp.size() < 4) {
                    temp+="0";
                }
                reverse(temp.begin(), temp.end());
                bin+=temp;
            }
        } else if ((args.back()[0] >= '0' && args.back()[0] <= '9') || args.back()[0] == '-'){
            int x = 0, mn = 1;
            for (int i = args.back().size()-1; i >= 0; --i) {
                if (args.back()[i] >= '0' && args.back()[i] <= '9') {
                    x+=mn*(args.back()[i]-'0');
                    mn*=10;
                }
            }
            if (args.back()[0] == '-') {
                unsigned int z = 0;
                z--;
                z-=(abs(x)-1);
                while (z > 0) {
                    bin+=char('0'+(z%2));
                    z>>=1;
                }
                reverse(bin.begin(), bin.end());
            } else {
                while (x > 0) {
                    bin+=char('0'+(x%2));
                    x>>=1;
                }
                reverse(bin.begin(), bin.end());
            }
        } else {
            cerr << "Not a number constant(:";
            exit(2);
        }
        args.back() = bin;
    }

    string result_bin = "", s1, s2;
    switch (type) {
        case 'r':
            if (operation == "srai" || operation == "sub" || operation == "sra") {
                result_bin+="0100000";
            } else if (operation == "slli" || operation == "srli"
                       || operation == "add" || operation == "sll"
                       || operation == "slt" || operation == "sltu"
                       || operation == "xor" || operation == "srl"
                       || operation == "or" || operation == "and") {
                result_bin+="0000000";
            } else {
                result_bin+="0000001";
            }
            if (operation == "slli" || operation == "srli" || operation == "srai") {
                reverse(args.back().begin(), args.back().end());
                while (args.back().size() < 5) {
                    args.back()+="0";
                }
                reverse(args.back().begin(), args.back().end());
                result_bin+=args.back();
            } else {
                result_bin+=get_register(args[3]);
            }
            result_bin+=get_register(args[2])+funct3[operation];
            result_bin+=get_register(args[1]);
            if (operation == "slli" || operation == "srli" || operation == "srai") {
                result_bin+="0010011";
            } else {
                result_bin+="0110011";
            }
            break;
        case 'i':
            reverse(args.back().begin(), args.back().end());
            while (args.back().size() < 12) {
                args.back()+="0";
            }
            s1 = args.back().substr(0, 12);
            reverse(s1.begin(), s1.end());
            result_bin+=s1+get_register(args[2]);
            result_bin+=funct3[operation]+get_register(args[1]);
            if (operation == "jalr") {
                result_bin+="1100111";
            } else if (operation == "lb" || operation == "lh" || operation == "lw"
                       || operation == "lbu" || operation == "lhu") {
                result_bin+="0000011";
            } else {
                result_bin+="0010011";
            }
            break;
        case 's':
            reverse(args.back().begin(), args.back().end());
            while (args.back().size() < 12) {
                args.back()+="0";
            }
            s1 = args.back().substr(5, 7);
            reverse(s1.begin(), s1.end());
            s2 = args.back().substr(0, 5);
            reverse(s2.begin(), s2.end());
            result_bin+=s1+get_register(args[2]);
            result_bin+=get_register(args[1])+funct3[operation];
            result_bin+=s2+"0100011";
            break;
        case 'b':
            reverse(args.back().begin(), args.back().end());
            while (args.back().size() < 13) {
                args.back()+="0";
            }
            s1 = args.back().substr(5, 6);
            reverse(s1.begin(), s1.end());
            s2 = args.back().substr(1, 4);
            reverse(s2.begin(), s2.end());
            result_bin+=args.back()[12]+s1;
            result_bin+=get_register(args[2])+get_register(args[1]);
            result_bin+=funct3[operation]+s2+args.back()[11];
            result_bin+="1100011";
            break;
        case 'u':
            for (int i = 0; i < 12; ++i) {
                args.back()+="0";
            }
            reverse(args.back().begin(), args.back().end());
            while (args.back().size() < 32) {
                args.back()+="0";
            }
            s1 = args.back().substr(12, 20);
            reverse(s1.begin(), s1.end());
            result_bin+=s1+get_register(args[1]);
            if (operation == "lui") {
                result_bin+="0110111";
            } else {
                result_bin+="0010111";
            }
            break;
        case 'j':
            reverse(args.back().begin(), args.back().end());
            while (args.back().size() < 32) {
                args.back()+="0";
            }
            s1 = args.back().substr(1, 10);
            reverse(s1.begin(), s1.end());
            s2 = args.back().substr(12, 8);
            reverse(s2.begin(), s2.end());
            result_bin+=args.back()[20]+s1;
            result_bin+=args.back()[11]+s2;
            result_bin+=get_register(args[1]);
            result_bin+="1101111";
            break;
        case 'f':
            if (operation == "fence.i") {
                result_bin = "00000000000000000001000000001111";
            } else {
                for (int i = 1; i <= 2; ++i) {
                    if (args[i][0] >= '0' && args[i][0] <= '9') {
                        reverse(args[i].begin(), args[i].end());
                        while (args[i].size() < 4) {
                            args.back()+="0";
                        }
                        reverse(args[i].begin(), args[i].end());
                    } else {
                        vector<int>temp(4, 0);
                        for (auto v : args[i]) {
                            if (v == 'i') temp[0] = 1;
                            else if (v == 'o') temp[1] = 1;
                            else if (v == 'r') temp[2] = 1;
                            else if (v == 'w') temp[3] = 1;
                        }
                        args[i].clear();
                        for (auto v : temp) {
                            args[i]+= to_string(v);
                        }
                    }
                }
                result_bin+="0000"+args[1]+args[2];
                result_bin+="00000000000000001111";
            }
            break;
        case 'e':
            if (operation == "ecall") {
                result_bin = "00000000000000000000000001110011";
            } else {
                result_bin = "00000000000100000000000001110011";
            }
    }
    return result_bin;
}

string int_to_string(int x) {
    if (x < 10) {
        return to_string(x);
    } else {
        x-=10;
        return string(1, 'a'+x);
    }
}

vector<char> OUT;

void parser(string answer) {
    if (answer.size() != 32) {
        cerr << "Incorrect translation(:";
        exit(3);
    } else {
        reverse(answer.begin(), answer.end());
        for (int i = 0; i < 4; ++i) {
            int cnt = 0;
            for (int j = 0; j < 8; ++j) {
                cnt+=(answer[i*8+j]-'0')*(1 << j);
            }
            OUT.push_back(char(cnt));
        }
    }
}

vector<vector<string>>kommands;

void reader() {
    string str;
    vector<string>commands;
    while (cin >> str) {
        string temp = "";
        str+=' ';
        for (auto v : str) {
            if (v == ' ' || v == ',') {
                if (!temp.empty()) {
                    commands.push_back(temp);
                    temp.clear();
                }
            } else {
                temp+=v;
            }
        }
    }
    int ind = 0;
    while (ind < commands.size()) {
        char type = get_type(commands[ind]);
        if (type == 'z') {
            cerr <<"Not supported command(:";
            exit(4);
        }
        int ind2 = ind+1;
        vector<string> assembler = {commands[ind]};
        while (ind2 < commands.size() && get_type(commands[ind2]) == 'z') {
            assembler.push_back(commands[ind2]);
            ++ind2;
        }
        int cnt = ind2-ind-1;
        if (((type == 'r' || type == 'i' || type == 's' || type == 'b') && cnt != 3)
            || ((type == 'u' || type == 'j' || type == 'f') && cnt != 2)) {
            cerr << "Incorrect number of arguments(:";
            exit(5);
        }
        kommands.push_back(assembler);
        parser(mashine_kode(assembler));
        ind = ind2;
    }
}

int string_to_int(string val, int bit_cnt) {
    if (val.size() >= 2 && val[0] == '0' && val[1] == 'x') {
        vector<int>bits(32, 0);
        int ind = 0;
        for (int i = val.size()-1; i >= 2; --i) {
            int x = 0;
            if (val[i] <= '9' && val[i] >= '0') {
                x = val[i]-'0';
            } else {
                x = 10+(val[i]-'a');
            }
            bits[ind] = (x%2);
            bits[ind+1] = ((x/2)%2);
            bits[ind+2] = ((x/4)%2);
            bits[ind+3] = ((x/8)%2);
            ind+=4;
        }
        int yk = 0;
        if (bits[bit_cnt-1] == 1) {
            yk = 1;
        }
        while (bit_cnt < 32) {
            bits[bit_cnt] = yk;
            bit_cnt++;
        }
        unsigned int mn = 1;
        int res = 0;
        for (auto v : bits) {
            res += v*mn;
            mn<<=1;
        }
        return res;
    }
    return stoi(val);
}

information get_adress(int ind) {
    return int_to_information((ind));
}

void operation(int i) {
    if (kommands[i][0] == "lui") {
        int val = string_to_int(kommands[i][2], 20) << 12;
        REGISTERS[get_int_register(kommands[i][1])] = val;
    } else if (kommands[i][0] == "auipc") {
        int val = string_to_int(kommands[i][2], 20) << 12;
        REGISTERS[get_int_register(kommands[i][1])] = PROGRAM_COUNTER + val;
    } else if (kommands[i][0] == "jal") {
        REGISTERS[get_int_register(kommands[i][1])] = PROGRAM_COUNTER + 4;
        PROGRAM_COUNTER += string_to_int(kommands[i][2], 21);
        return;
    } else if (kommands[i][0] == "jalr") {
        int t = PROGRAM_COUNTER + 4;
        PROGRAM_COUNTER = (REGISTERS[get_int_register(kommands[i][3])] + string_to_int(kommands[i][2], 12)) & (~1);
        REGISTERS[get_int_register(kommands[i][1])] = t;
        return;
    } else if (kommands[i][0] == "beq") {
        int s1 = REGISTERS[get_int_register(kommands[i][1])];
        int s2 = REGISTERS[get_int_register(kommands[i][2])];
        if (s1 == s2) {
            PROGRAM_COUNTER += string_to_int(kommands[i][3], 13);
            return;
        }
    } else if (kommands[i][0] == "bne") {
        int s1 = REGISTERS[get_int_register(kommands[i][1])];
        int s2 = REGISTERS[get_int_register(kommands[i][2])];
        if (s1 != s2) {
            PROGRAM_COUNTER += string_to_int(kommands[i][3], 13);
            return;
        }
    } else if (kommands[i][0] == "blt") {
        int s1 = REGISTERS[get_int_register(kommands[i][1])];
        int s2 = REGISTERS[get_int_register(kommands[i][2])];
        if (s1 < s2) {
            PROGRAM_COUNTER += string_to_int(kommands[i][3], 13);
            return;
        }
    } else if (kommands[i][0] == "bge") {
        int s1 = REGISTERS[get_int_register(kommands[i][1])];
        int s2 = REGISTERS[get_int_register(kommands[i][2])];
        if (s1 >= s2) {
            PROGRAM_COUNTER += string_to_int(kommands[i][3], 13);
            return;
        }
    } else if (kommands[i][0] == "bltu") {
        unsigned int s1 = REGISTERS[get_int_register(kommands[i][1])];
        unsigned int s2 = REGISTERS[get_int_register(kommands[i][2])];
        if (s1 < s2) {
            PROGRAM_COUNTER += string_to_int(kommands[i][3], 13);
            return;
        }
    } else if (kommands[i][0] == "bgeu") {
        unsigned int s1 = REGISTERS[get_int_register(kommands[i][1])];
        unsigned int s2 = REGISTERS[get_int_register(kommands[i][2])];
        if (s1 >= s2) {
            PROGRAM_COUNTER += string_to_int(kommands[i][3], 13);
            return;
        }
    } else if (kommands[i][0] == "lb") {
        int ind = REGISTERS[get_int_register(kommands[i][3])] + string_to_int(kommands[i][2], 12);
        int val = memory[ind]&0xff;
        REGISTERS[get_int_register(kommands[i][1])] = val;

        information adress = get_adress(ind);
        cache_lru.update_lru(adress, 1, false);
        cache_plru.update_plru(adress, 1, false);

    } else if (kommands[i][0] == "lh") {
        int ind = REGISTERS[get_int_register(kommands[i][3])] + string_to_int(kommands[i][2], 12);
        int val = memory[ind]&0xffff;
        REGISTERS[get_int_register(kommands[i][1])] = val;

        information adress = get_adress(ind);
        cache_lru.update_lru(adress, 1, false);
        cache_plru.update_plru(adress, 1, false);

    } else if (kommands[i][0] == "lw") {
        int ind = REGISTERS[get_int_register(kommands[i][3])] + string_to_int(kommands[i][2], 12);
        int val = memory[ind]&0xffffffff;
        REGISTERS[get_int_register(kommands[i][1])] = val;

        information adress = get_adress(ind);
        cache_lru.update_lru(adress, 1, false);
        cache_plru.update_plru(adress, 1, false);

    } else if (kommands[i][0] == "lbu") {
        int ind = REGISTERS[get_int_register(kommands[i][3])] + string_to_int(kommands[i][2], 12);
        unsigned int val = memory[ind]&0xff;
        REGISTERS[get_int_register(kommands[i][1])] = val;

        information adress = get_adress(ind);
        cache_lru.update_lru(adress, 1, false);
        cache_plru.update_plru(adress, 1, false);

    } else if (kommands[i][0] == "lhu") {
        int ind = REGISTERS[get_int_register(kommands[i][3])] + string_to_int(kommands[i][2], 12);
        unsigned int val = memory[ind]&0xffff;
        REGISTERS[get_int_register(kommands[i][1])] = val;

        information adress = get_adress(ind);
        cache_lru.update_lru(adress, 1, false);
        cache_plru.update_plru(adress, 1, false);

    } else if (kommands[i][0] == "sb") {
        int ind = REGISTERS[get_int_register(kommands[i][3])] + string_to_int(kommands[i][2], 12);
        memory[ind] = REGISTERS[get_int_register(kommands[i][1])]&0xff;

        information adress = get_adress(ind);
        cache_lru.update_lru(adress, 1, true);
        cache_plru.update_plru(adress, 1, true);

    } else if (kommands[i][0] == "sh") {
        int ind = REGISTERS[get_int_register(kommands[i][3])] + string_to_int(kommands[i][2], 12);
        memory[ind] = REGISTERS[get_int_register(kommands[i][1])]&0xffff;

        information adress = get_adress(ind);
        cache_lru.update_lru(adress, 1, true);
        cache_plru.update_plru(adress, 1, true);

    } else if (kommands[i][0] == "sw") {
        int ind = REGISTERS[get_int_register(kommands[i][3])] + string_to_int(kommands[i][2], 12);
        memory[ind] = REGISTERS[get_int_register(kommands[i][1])]&0xffffffff;

        information adress = get_adress(ind);
        cache_lru.update_lru(adress, 1, true);
        cache_plru.update_plru(adress, 1, true);

    } else if (kommands[i][0] == "addi") {
        REGISTERS[get_int_register(kommands[i][1])] = REGISTERS[get_int_register(kommands[i][2])] + string_to_int(kommands[i][3], 12);
    } else if (kommands[i][0] == "slti") {
        if (REGISTERS[get_int_register(kommands[i][2])] < string_to_int(kommands[i][3], 12)) {
            REGISTERS[get_int_register(kommands[i][1])] = 1;
        } else {
            REGISTERS[get_int_register(kommands[i][1])] = 0;
        }
    } else if (kommands[i][0] == "sltiu") {
        int s1 = REGISTERS[get_int_register(kommands[i][2])];
        unsigned int s2 = string_to_int(kommands[i][3], 12);
        if (s1 < s2) {
            REGISTERS[get_int_register(kommands[i][1])] = 1;
        } else {
            REGISTERS[get_int_register(kommands[i][1])] = 0;
        }
    } else if (kommands[i][0] == "xori") {
        REGISTERS[get_int_register(kommands[i][1])] = REGISTERS[get_int_register(kommands[i][2])] ^ string_to_int(kommands[i][3], 12);
    } else if (kommands[i][0] == "ori") {
        REGISTERS[get_int_register(kommands[i][1])] = REGISTERS[get_int_register(kommands[i][2])] | string_to_int(kommands[i][3], 12);
    } else if (kommands[i][0] == "andi") {
        REGISTERS[get_int_register(kommands[i][1])] = REGISTERS[get_int_register(kommands[i][2])] & string_to_int(kommands[i][3], 12);
    } else if (kommands[i][0] == "slli") {
        REGISTERS[get_int_register(kommands[i][1])] = REGISTERS[get_int_register(kommands[i][2])] << string_to_int(kommands[i][3], 5);
    } else if (kommands[i][0] == "srli") {
        REGISTERS[get_int_register(kommands[i][1])] = static_cast<uint32_t>(REGISTERS[get_int_register(kommands[i][2])]) >> string_to_int(kommands[i][3], 5);
    } else if (kommands[i][0] == "srai") {
        REGISTERS[get_int_register(kommands[i][1])] = REGISTERS[get_int_register(kommands[i][2])] >> string_to_int(kommands[i][3], 5);
    } else if (kommands[i][0] == "add") {
        REGISTERS[get_int_register(kommands[i][1])] = REGISTERS[get_int_register(kommands[i][2])] + REGISTERS[get_int_register(kommands[i][3])];
    } else if (kommands[i][0] == "sub") {
        REGISTERS[get_int_register(kommands[i][1])] = REGISTERS[get_int_register(kommands[i][2])] - REGISTERS[get_int_register(kommands[i][3])];
    } else if (kommands[i][0] == "sll") {
        REGISTERS[get_int_register(kommands[i][1])] = REGISTERS[get_int_register(kommands[i][2])] << REGISTERS[get_int_register(kommands[i][3])];
    } else if (kommands[i][0] == "slt") {
        if (REGISTERS[get_int_register(kommands[i][2])] < REGISTERS[get_int_register(kommands[i][3])]) {
            REGISTERS[get_int_register(kommands[i][1])] = 1;
        } else {
            REGISTERS[get_int_register(kommands[i][1])] = 0;
        }
    } else if (kommands[i][0] == "sltu") {
        int s1 = REGISTERS[get_int_register(kommands[i][2])];
        unsigned int s2 = REGISTERS[get_int_register(kommands[i][3])];
        if (s1 < s2) {
            REGISTERS[get_int_register(kommands[i][1])] = 1;
        } else {
            REGISTERS[get_int_register(kommands[i][1])] = 0;
        }
    } else if (kommands[i][0] == "xor") {
        REGISTERS[get_int_register(kommands[i][1])] = REGISTERS[get_int_register(kommands[i][2])] ^ REGISTERS[get_int_register(kommands[i][3])];
    } else if (kommands[i][0] == "srl") {
        REGISTERS[get_int_register(kommands[i][1])] = static_cast<uint32_t>(REGISTERS[get_int_register(kommands[i][2])]) >> REGISTERS[get_int_register(kommands[i][3])];
    } else if (kommands[i][0] == "sra") {
        REGISTERS[get_int_register(kommands[i][1])] = REGISTERS[get_int_register(kommands[i][2])] >> REGISTERS[get_int_register(kommands[i][3])];
    } else if (kommands[i][0] == "or") {
        REGISTERS[get_int_register(kommands[i][1])] = REGISTERS[get_int_register(kommands[i][2])] | REGISTERS[get_int_register(kommands[i][3])];
    } else if (kommands[i][0] == "and") {
        REGISTERS[get_int_register(kommands[i][1])] = REGISTERS[get_int_register(kommands[i][2])] & REGISTERS[get_int_register(kommands[i][3])];
    } else if (kommands[i][0] == "mul") {
        long long t = REGISTERS[get_int_register(kommands[i][2])];
        t *= REGISTERS[get_int_register(kommands[i][3])];
        REGISTERS[get_int_register(kommands[i][1])] = t%(1ll << 32);
    } else if (kommands[i][0] == "mulh") {
        long long t = REGISTERS[get_int_register(kommands[i][2])];
        t *= REGISTERS[get_int_register(kommands[i][3])];
        REGISTERS[get_int_register(kommands[i][1])] = (t >> 32);
    } else if (kommands[i][0] == "mulhsu") {
        long long t = REGISTERS[get_int_register(kommands[i][2])];
        t *= (unsigned int)(REGISTERS[get_int_register(kommands[i][3])]);
        REGISTERS[get_int_register(kommands[i][1])] = (t >> 32);
    } else if (kommands[i][0] == "mulhu") {
        long long t = (unsigned int)(REGISTERS[get_int_register(kommands[i][2])]);
        t *= (unsigned int)(REGISTERS[get_int_register(kommands[i][3])]);
        REGISTERS[get_int_register(kommands[i][1])] = t >> 32;
    } else if (kommands[i][0] == "div") {
        REGISTERS[get_int_register(kommands[i][1])] = REGISTERS[get_int_register(kommands[i][2])] / REGISTERS[get_int_register(kommands[i][3])];
    } else if (kommands[i][0] == "divu") {
        REGISTERS[get_int_register(kommands[i][1])] = (unsigned int)(REGISTERS[get_int_register(kommands[i][2])]) / (unsigned int)(REGISTERS[get_int_register(kommands[i][3])]);
    } else if (kommands[i][0] == "rem") {
        REGISTERS[get_int_register(kommands[i][1])] = REGISTERS[get_int_register(kommands[i][2])] % REGISTERS[get_int_register(kommands[i][3])];
    } else if (kommands[i][0] == "remu") {
        REGISTERS[get_int_register(kommands[i][1])] = (unsigned int)(REGISTERS[get_int_register(kommands[i][2])]) % (unsigned int)(REGISTERS[get_int_register(kommands[i][3])]);
    }
    PROGRAM_COUNTER += 4;
}

int main(int argc, char* argv[]) {
    int input = -1, output = -1;
    int i = 1;
    while (i < argc) {
        if (strcmp(argv[i], "--asm") == 0) {
            input = i+1;
            i++;
        } else if (strcmp(argv[i], "--bin") == 0) {
            output = i+1;
            i++;
        }
        i++;
    }
    if (min(input, output) == -1) {
        return 0;
    }
    freopen(argv[input], "r", stdin);
    reader();
    while (PROGRAM_COUNTER < 4*kommands.size()+0x10000 && PROGRAM_COUNTER >= 0x10000) {
        // зануляем zero
        REGISTERS[0] = 0;
        information adress = int_to_information(PROGRAM_COUNTER);
        //LRU
        cache_lru.update_lru(adress, 0, false);
        //pLRU
        cache_plru.update_plru(adress, 0, false);
        int number_of_kommand = (PROGRAM_COUNTER-0x10000)/4;
        operation(number_of_kommand);
    }
    vector<double>answer(6);
    answer[0] = 100*double(cache_hits_lru[0]+cache_hits_lru[1])/(cnt_lru[0]+cnt_lru[1]);
    answer[1] = 100*double(cache_hits_lru[0])/(cnt_lru[0]);
    answer[2] = 100*double(cache_hits_lru[1])/(cnt_lru[1]);
    answer[3] = 100*double(cache_hits_plru[0]+cache_hits_plru[1])/(cnt_plru[0]+cnt_plru[1]);
    answer[4] = 100*double(cache_hits_plru[0])/(cnt_plru[0]);
    answer[5] = 100*double(cache_hits_plru[1])/(cnt_plru[1]);
    printf("replacement\thit rate\thit rate (inst)\thit rate (data)\n");
    printf("        LRU\t%3.5f%%\t%3.5f%%\t%3.5f%%\n", abs(answer[0]), abs(answer[1]), abs(answer[2]));
    printf("       pLRU\t%3.5f%%\t%3.5f%%\t%3.5f%%\n", abs(answer[3]), abs(answer[4]), abs(answer[5]));
    freopen(argv[output], "w", stdout);
    for (auto v : OUT) {
        cout << v;
    }
    return 0;
}