#!/usr/bin/env python3


def gen_color(h, s, v):
    c = v * s
    h2 = h / 60.0
    x = c * (1 - abs(h2 % 2.0 - 1))
    if 0 <= h2 < 1:
        r = c
        g = x
        b = 0
    elif 1 <= h2 < 2:
        r = x
        g = c
        b = 0
    elif 2 <= h2 < 3:
        r = 0
        g = c
        b = x
    elif 3 <= h2 < 4:
        r = 0
        g = x
        b = c
    elif 4 <= h2 < 5:
        r = x
        g = 0
        b = c
    elif 5 <= h2 < 6:
        r = c
        g = 0
        b = x
    else:
        r = 0
        g = 0
        b = 0
    m = v - c
    rgb = (r + m, g + m, b + m)
    r = int(max(0, min(255, rgb[0] * 255)))
    g = int(max(0, min(255, rgb[1] * 255)))
    b = int(max(0, min(255, rgb[2] * 255)))
    return "%02x%02x%02x" % (r, g, b)


def html_color(rgb):
    print("        <div class=\"cell\" style=\"background: #%s\"></div>" % rgb)


def hex_colors():
    for h in range(0, 360, 30):
        yield gen_color(h, 0.6, 0.80)
        yield gen_color(h, 0.9, 0.9)
        # for s in [0.6, 0.9]:
        #    for v in [0.9]:
        #        html_color(gen_color(h, s, v))


def print_html():
    print("""<html>
    <head>
        <style>
            .cell {
                width: 40px;
                height: 40px;
                float: left;
            }
        </style>
    </head>
    <body>""")
    for rgb in hex_colors():
        html_color(rgb)
    print("<div>")
    for rgb in hex_colors():
        print("add(0xff%s);<br>" % rgb)
    print("</div>")
    print("""    </body></html>""")


print_html()
