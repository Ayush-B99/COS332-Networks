# 🌐 COS 332 — Computer Networks

> A comprehensive collection of practical implementations exploring the foundations of modern computer networking.

---

## 📌 Overview

This repository contains a series of practical assignments developed as part of **COS 332 (Computer Networks)** at the University of Pretoria.  
The focus of this module is on **low-level networking concepts**, where protocols are not just used — but **implemented from scratch**.

Unlike typical high-level development, this work emphasizes:
- Direct **socket-level communication**
- Manual handling of **network protocols**
- Understanding how systems communicate across layers

> ⚠️ The core philosophy: "Don’t use the protocol — build the protocol."

---

## 🧠 What This Repo Covers

### 🔌 Core Networking Concepts
- Client–Server architecture
- Socket programming (TCP-based communication)
- Stateful vs stateless communication
- Protocol design and parsing

### 🌍 Application Layer Protocols
- HTTP (custom servers & browser interaction)
- SMTP (sending email manually)
- POP3 (mail retrieval systems)
- FTP (file transfer & synchronization)
- LDAP (directory services)

### 🖥️ System-Level Networking
- Building and deploying servers
- Working with Unix/Linux environments
- Virtual machines & isolated networking setups
- Network debugging and traffic inspection

### 🔐 Security & Network Control
- Firewall concepts (Layer 3 vs Layer 7)
- Application proxies
- Traffic filtering and inspection

---

## 🧪 Practical Approach

Each component in this repository is designed to:
- Reinforce **theoretical networking concepts**
- Translate RFC specifications into **working systems**
- Encourage **problem-solving at protocol level**

Rather than relying on frameworks or libraries, implementations:
- Use **raw sockets**
- Handle **message construction and parsing manually**
- Simulate real-world network interactions

---

## 🛠️ Tech & Tools

- Java / Low-level languages
- TCP Sockets
- HTTP, SMTP, FTP, POP3, LDAP
- Linux / Unix / Virtual Machines
- Wireshark, Telnet, Apache

---

## ⚙️ Environment & Setup

This repository is designed to be run in a **network-capable environment**, typically:

- Linux / Unix-based systems
- Virtual machines (e.g. VirtualBox)
- Localhost or LAN-based communication
- Optional: Raspberry Pi / external host setup

---

## 📂 Repository Structure

/src        → Source code implementations  
/docs       → Notes and references  
/scripts    → Setup helpers  
/tests      → Testing utilities  

---

## 🎯 Learning Outcomes

- Deep understanding of **network protocols**
- Ability to implement **client-server systems from scratch**
- Experience with **real-world networking constraints**
- Debugging communication at **packet/message level**
- Translating RFC documentation into **working code**

---

## ⚠️ Important Notes

- No high-level networking libraries are used for core functionality  
- All protocol communication is handled manually  
- Focus is on understanding, not just functionality  

---

## 👨‍💻 Author

Ayush Beekum  
Computer Science Student — University of Pretoria  

---

## 🚀 Final Thoughts

This repository represents a deep dive into how the internet works under the hood.

From raw sockets to full protocol flows — everything here is built, not abstracted.
