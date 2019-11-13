class MyLocationStyle {
  int interval;
  FillColor fillColor;
  StrokeColor strokeColor;

  MyLocationStyle({this.interval, this.fillColor, this.strokeColor});

  MyLocationStyle.fromJson(Map<String, dynamic> json) {
    interval = json['interval'];
    fillColor = json['fillColor'] != null
        ? new FillColor.fromJson(json['fillColor'])
        : null;
    strokeColor = json['strokeColor'] != null
        ? new StrokeColor.fromJson(json['strokeColor'])
        : null;
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['interval'] = this.interval;
    if (this.fillColor != null) {
      data['fillColor'] = this.fillColor.toJson();
    }
    if (this.strokeColor != null) {
      data['strokeColor'] = this.strokeColor.toJson();
    }
    return data;
  }
}

class FillColor {
  int a;
  int r;
  int g;
  int b;

  FillColor({this.a = 0, this.r = 0, this.g = 0, this.b = 0});

  FillColor.fromJson(Map<String, dynamic> json) {
    a = json['a'];
    r = json['r'];
    g = json['g'];
    b = json['b'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['a'] = this.a;
    data['r'] = this.r;
    data['g'] = this.g;
    data['b'] = this.b;
    return data;
  }
}

class StrokeColor {
  int a;
  int r;
  int g;
  int b;

  StrokeColor({this.a, this.r, this.g, this.b});

  StrokeColor.fromJson(Map<String, dynamic> json) {
    a = json['a'];
    r = json['r'];
    g = json['g'];
    b = json['b'];
  }

  Map<String, dynamic> toJson() {
    final Map<String, dynamic> data = new Map<String, dynamic>();
    data['a'] = this.a;
    data['r'] = this.r;
    data['g'] = this.g;
    data['b'] = this.b;
    return data;
  }
}
