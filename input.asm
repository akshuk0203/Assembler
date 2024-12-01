section .bss
	a resd 3
	b resd 1
	c resw 1
	d resb 8
	e resd 10
	
section .text
	global main
	
section .data
	doublebyte1 dd 5
	doublebyte dd 5000000
	arr dd 10,32,44
	msg db "AB CDE",0
	doubleword dw 12345
	doubleword1 dw 12
	mixed db "abc123",10,0
	
main:
	mov eax,ebx
	add dword[a],10
	add dword[b],1000000
	add eax,dword[a]
	add ebx,dword[a]
	add eax,10
	add eax,1000000000
	add ebx,1000000000
	add ebx,20
	add eax,ebx
	add dword[b],ebx
	jmp loop1
	add dword[b],eax
	xor eax,100000000
	xor ebx,100000000
	xor ebx,10
	xor dword[a],100
	xor dword[a],1000
	xor dword[a],100000
	xor dword[a],1000000000
	xor ebx,ecx
	xor dword[a],ebx
	xor ebx,dword[a]
	inc dword[a]
	inc ebx
	inc eax
	dec ebx
	dec eax
	div eax
	div ebx
loop:	div dword[a]
	mul ebx
	mul dword[b]
	mul dword[a]
	mul ecx
	jnz loop
loop1: inc ebx
	jmp loop

section .data
	ew dd 5
