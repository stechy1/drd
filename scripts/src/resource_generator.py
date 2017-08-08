#!/usr/bin/env python3

import os

folder = './src/main/resources/'
string = 'public static final String {} = "{}";\n'
output_path = './src/main/java/cz/stechy/drd/R.java'

configuration = [
    'offline_database_name', 'use_online_database', 'online_database_credentials_path', 'hero_levelup_points_per_level'
]

def generate_resources(root_folder, requested_extension, class_name, output_file):
  output_file.write("    public static class " + class_name + " {\n\n")
  for dirname, dirs, files in os.walk(root_folder):
    for filename in files:
      filename_without_extension, extension = os.path.splitext(filename)
      if extension == ('.' + requested_extension):
        output_file.write('        ' + string.format(filename_without_extension.upper(), filename_without_extension))
  output_file.write("    }\n\n")


def generate_translate_keys(translate_file, output_file):
  output_file.write("    public static class Translate {\n\n")
  with open(translate_file, "r", encoding="utf8") as f:
    lines = f.readlines()
    for line in lines:
      if "=" not in line:
        continue
      key = line[:line.index("=")]
      output_file.write('        ' + string.format(key.upper()[4:], key))
  output_file.write("    }\n\n")

def generate_config_keys(config, output_file):
  output_file.write("    public static class Config {\n\n")
  for key in config:
    output_file.write('        ' + string.format(key.upper(), key))
  output_file.write("    }\n")

with open(output_path, "w", encoding="utf8") as file:
  file.write("package cz.stechy.drd;\n\n")
  file.write('@SuppressWarnings("unused")\n')
  file.write("public final class R {\n\n")

  generate_resources(folder + "fxml", "fxml", "FXML", file)
  generate_translate_keys(folder + "lang/translate_cs_CZ.properties", file)
  generate_config_keys(configuration, file)
  file.write("}\n")
