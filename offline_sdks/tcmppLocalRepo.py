import os
import requests
from lxml import etree
import argparse

def download_file(url, local_path):
    response = requests.get(url)
    response.raise_for_status()

    os.makedirs(os.path.dirname(local_path), exist_ok=True)
    with open(local_path, 'wb') as f:
        f.write(response.content)

def download_dependency(dependency, base_url, local_repo):
    group_id, artifact_id, version, ext= dependency
    group_path = group_id.replace('.', '/')
    artifact_base_url = f"{base_url}/{group_path}/{artifact_id}/{version}"
    local_repo_path = f"{local_repo}/{group_path}/{artifact_id}/{version}"

    jar_url = f"{artifact_base_url}/{artifact_id}-{version}.{ext}"
    jar_local_path = f"{local_repo_path}/{artifact_id}-{version}.{ext}"
    download_file(jar_url, jar_local_path)

    pom_url = f"{artifact_base_url}/{artifact_id}-{version}.pom"
    pom_local_path = f"{local_repo_path}/{artifact_id}-{version}.pom"
    download_file(pom_url, pom_local_path)

def main(local_repo = "offline_sdks/tcmpp-local-repo"):
    base_url = "https://maven-dev.tcmppcloud.com/fHKFBbEjd/repository/maven-public/"
    dependencies = [
        ("com.tencent.tcmpp.android", "mini_annotation", "1.5.1", "jar"),
        ("com.tencent.tcmpp.android", "mini_annotation_processor", "1.5.3", "jar"),
        ("com.squareup", "javapoet", "1.11.1", "jar")
    ]

    for dependency in dependencies:
        download_dependency(dependency, base_url, local_repo)

if __name__ == "__main__":
    parser = argparse.ArgumentParser(
        description="Download maven artifacts into a local maven-style repository"
    )
    parser.add_argument(
        "local_repo",
        nargs="?",
        default="offline_sdks/tcmpp-local-repo",
        help="local repository path to write downloaded artifacts (default: offline_sdks/tcmpp-local-repo)",
    )
    args = parser.parse_args()

    main(local_repo=args.local_repo)