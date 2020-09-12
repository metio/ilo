Name:           ilo
Version:        2020.9.7
Release:        1%{?dist}
Summary:        Manage Reproducible Build Environments
License:        CC0
URL:            https://ilo.projects.metio.wtf/
BuildArch:      x86_64
Source0:        https://github.com/metio/%{name}/releases/download/%{version}-34731/%{name}-%{version}-34731-linux.zip
Requires:       podman
BuildRequires:  unzip
BuildRequires:  curl

%description
Manage Reproducible Build Environments

%prep
/usr/bin/curl --location https://github.com/metio/%{name}/releases/download/%{version}-34731/%{name}-%{version}-34731-linux.zip --output ilo.zip

%build
/usr/bin/unzip ilo.zip

%install
mkdir -p %{buildroot}/%{_bindir}
install -m 0755 %{name}-%{version}-34731/%{name} %{buildroot}/%{_bindir}/%{name}

%check
# no checks yet

%files
%license %{name}-%{version}-34731/LICENSE
%{_bindir}/%{name}

%changelog
# see release page for changes
