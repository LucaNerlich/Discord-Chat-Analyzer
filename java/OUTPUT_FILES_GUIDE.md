# Discord Chat Analyzer - Output Files Guide

## 📁 Output Directory Structure

When you run the Discord Chat Analyzer, it creates the following files in your output directory:

```
logs/
├── m10z/
│   └── output/
│       ├── output-all.json                          # Complete user data
│       ├── ranking-most-messages.json               # Users by message count
│       ├── ranking-account-age.json                 # Users by account age
│       ├── ranking-most-common-reaction.json        # Most used reactions
│       ├── ranking-avg-word-count.json              # Average words per message
│       ├── ranking-most-embeds.json                 # Users with most embeds
│       ├── ranking-most-attachments.json            # Users with most attachments
│       ├── ranking-times-mentioned.json             # Users mentioned most often
│       ├── ranking-mention-network.json             # 🆕 Complete mention network analysis
│       ├── ranking-most-mentions-sent.json          # 🆕 Users who mention others most
│       ├── ranking-social-graph-matrix.json         # 🆕 Full social relationship matrix
│       ├── social-graph-analysis.txt                # 🆕 Human-readable social graph analysis
│       └── social-graph.html                        # 🆕 Interactive network visualization
└── dttd/
    └── output/
        └── [same structure as above]
```

## 📊 New Social Graph Files

### 1. `social-graph-analysis.txt`
**Human-readable text analysis of your Discord community's social network**

**Contains:**
- Network statistics (total users, mentions, connections)
- Most connected users ranking
- Top mutual relationships
- Simple ASCII network diagram

**Sample Content:**
```
=== SOCIAL GRAPH ANALYSIS ===

Network Statistics:
- Total Users: 45
- Total Mentions: 2,847
- Total Connections: 234
- Avg Connections/User: 5.20

=== MOST CONNECTED USERS ===
 1. Alice               (Connections: 12, Sent: 145, Received: 89)
 2. Bob                 (Connections: 10, Sent: 98, Received: 123)

=== TOP MUTUAL RELATIONSHIPS ===
 1. Alice→Bob: 25, Bob→Alice: 18 (Total: 43)
 2. Charlie→Dave: 19, Dave→Charlie: 15 (Total: 34)

=== NETWORK DIAGRAM ===
Alice           ──→ Bob (25)
Bob             ──→ Alice (18)
Charlie         ──→ Dave (19)
```

### 2. `social-graph.html`
**Interactive web-based network visualization**

**Features:**
- **Interactive nodes** you can click and drag
- **Hover tooltips** showing mention statistics
- **Zoom and pan** for exploring large networks
- **Physics simulation** for natural node arrangement
- **Network statistics panel** with key metrics

**How to use:**
1. Open `social-graph.html` in any web browser
2. Hover over users to see their mention statistics
3. Click and drag nodes to reorganize the network
4. Use mouse wheel to zoom in/out
5. Right-click and drag to pan around the network

### 3. `ranking-mention-network.json`
**Complete mention network analysis data**

**Contains:**
- `mostMentionsSent`: Users ranked by mentions sent
- `topMentionRelationships`: Top 20 mention relationships
- `mentionMatrix`: Complete user-to-user mention matrix
- `totalMentionConnections`: Total network connections

### 4. `ranking-most-mentions-sent.json`
**Users ranked by how many mentions they send**

**Contains:**
- `mostMentionsSent`: Users ranked by mentions sent to others
- `totalMentionsSent`: Total mentions sent in the community

### 5. `ranking-social-graph-matrix.json`
**Complete social relationship matrix**

**Contains:**
- `socialGraphMatrix`: Full user-to-user mention matrix
- `userGraphStats`: Per-user network statistics including:
  - Incoming/outgoing connections
  - Total mentions sent/received
  - Centrality scores (social influence measure)

## 🎯 What You Can Learn

### **Social Influencers**
- Users with highest centrality scores
- Most connected community members
- Communication hubs that connect different groups

### **Communication Patterns**
- Who mentions whom most frequently
- Mutual vs. one-way relationships
- Community clusters and sub-groups

### **Network Health**
- How connected your community is
- Average relationship strength
- Isolated users who might need more engagement

## 🔧 Using the Data

### **For Community Management:**
- Identify key community members
- Find users who might need more engagement
- Understand communication flow patterns

### **For Further Analysis:**
- Import JSON files into data analysis tools
- Open HTML file for interactive exploration
- Use text file for quick insights and reports

### **For Visualization:**
- The HTML file works in any web browser
- JSON data can be imported into network analysis tools
- Text analysis provides ready-made insights

## 📈 Example Analysis Workflow

1. **Start with text file** (`social-graph-analysis.txt`) for quick overview
2. **Open HTML file** (`social-graph.html`) for interactive exploration
3. **Use JSON files** for detailed data analysis or custom visualizations
4. **Combine with existing rankings** for comprehensive community analysis

All files are automatically generated when you run the main analyzer - no additional setup required! 