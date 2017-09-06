#!/usr/bin/env python3

import os

folder = './src/main/resources/'
string = 'public static final String {} = "{}";\n'
constant_builder = 'public static final {} {} = {};\n'
output_path = './src/main/java/cz/stechy/drd/R.java'

constants = [
	{'name': 'DATABASE_VERSION', 'type': 'int', 'value': '1'}
]

configuration = [
    'offline_database_name', 'use_online_database', 'online_database_credentials_path', 'hero_levelup_points_per_level', 'database_version'
]

def write_key_pair_values(output_file):
  for constant in constants:
    output_file.write("    " + constant_builder.format(constant['type'], constant['name'], constant['value']))
  output_file.write("\n")

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

  write_key_pair_values(file)
  generate_resources(folder + "fxml", "fxml", "FXML", file)
  generate_translate_keys(folder + "lang/translate_cs_CZ.properties", file)
  generate_config_keys(configuration, file)
  file.write("}\n")
