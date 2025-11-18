# Security Policy

## Supported Versions

We release patches for security vulnerabilities in the following versions:

| Version | Supported          |
| ------- | ------------------ |
| 2.0.x   | :white_check_mark: |
| 1.0.x   | :x:                |

## Reporting a Vulnerability

**Please do not report security vulnerabilities through public GitHub issues.**

If you discover a security vulnerability within this project, please send an email to the maintainers through GitHub's private vulnerability reporting feature:

1. Go to the [Security tab](https://github.com/lukester814/hi-alch-bot/security)
2. Click "Report a vulnerability"
3. Provide a detailed description

Alternatively, you can create a new issue with the `security` label, but please be careful not to disclose sensitive details publicly.

### What to Include

When reporting a vulnerability, please include:

- Type of vulnerability (e.g., code injection, credential exposure, etc.)
- Full paths of source file(s) related to the manifestation of the vulnerability
- The location of the affected source code (tag/branch/commit or direct URL)
- Any special configuration required to reproduce the issue
- Step-by-step instructions to reproduce the issue
- Proof-of-concept or exploit code (if possible)
- Impact of the issue, including how an attacker might exploit it

### Response Time

- We will acknowledge your email within 48 hours
- We will provide a more detailed response within 7 days
- We will work on a fix and notify you of the release timeline

## Security Considerations

### This is an Educational Project

**IMPORTANT**: This project is for educational purposes only. Using bots in Old School RuneScape violates the game's Terms of Service and can result in account bans.

### Known Security Considerations

1. **Discord Webhooks**
   - Webhook URLs should be kept private
   - Never commit webhook URLs to version control
   - Treat webhooks like passwords

2. **Configuration Files**
   - Never commit configuration files with sensitive data
   - Use environment variables for sensitive information
   - Check `.gitignore` before committing

3. **API Keys & Credentials**
   - This bot should never require your OSRS credentials
   - Be suspicious of any forks asking for account information
   - Only use on accounts you don't care about

4. **Third-Party Dependencies**
   - DreamBot API is trusted, but verify downloads
   - Be cautious of unofficial DreamBot distributions
   - Keep dependencies up to date

5. **Code Injection**
   - Custom item search validates input
   - SQL injection not applicable (no database)
   - XSS not applicable (no web interface)

## Best Practices

### For Users

1. **Never share:**
   - Discord webhook URLs
   - Account credentials
   - Personal information

2. **Always:**
   - Review code before running
   - Use on alternate accounts only
   - Keep the bot updated
   - Report suspicious behavior

3. **Be aware:**
   - Botting violates OSRS Terms of Service
   - You could be banned
   - Use at your own risk

### For Contributors

1. **Do not:**
   - Commit secrets, API keys, or credentials
   - Add dependencies without review
   - Implement features that collect user data
   - Add telemetry or analytics

2. **Always:**
   - Validate user input
   - Use parameterized queries (if applicable)
   - Sanitize file paths
   - Review security implications of changes

3. **Consider:**
   - Could this feature be abused?
   - Does this expose user data?
   - Is this dependency trustworthy?
   - Are there safer alternatives?

## Vulnerability Types We Care About

### High Priority

- **Code Execution**: Arbitrary code execution vulnerabilities
- **Credential Theft**: Features that could steal credentials
- **Data Exposure**: Exposing user data or configurations
- **Malicious Dependencies**: Compromised or malicious libraries

### Medium Priority

- **Input Validation**: Improper input validation
- **Path Traversal**: Directory traversal vulnerabilities
- **Information Disclosure**: Leaking sensitive information

### Low Priority (but still report them!)

- **Denial of Service**: DoS vulnerabilities
- **Minor Information Leaks**: Non-sensitive information disclosure

## Security Updates

Security updates will be released as patch versions (e.g., 2.0.1) and announced via:

- GitHub Security Advisories
- Release notes
- README updates

## Disclaimer

This project is provided "as is" without warranty of any kind. The maintainers are not responsible for:

- Account bans or penalties from Jagex
- Loss of in-game items or progress
- Any damages resulting from use of this software
- Security vulnerabilities in third-party dependencies

## Resources

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [GitHub Security Best Practices](https://docs.github.com/en/code-security)
- [Contributor Covenant](https://www.contributor-covenant.org/)

---

**Remember**: This is an educational project. Always bot responsibly and at your own risk.
